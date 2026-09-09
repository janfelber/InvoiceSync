package com.invoicesync.modules.subscription.service;

import static com.invoicesync.core.enums.SubscriptionPlan.NONE;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.config.StripeConfig;
import com.invoicesync.core.enums.SubscriptionPlan;
import com.invoicesync.core.exception.NoActiveSubscriptionException;
import com.invoicesync.modules.stripe.StripeMapper;
import com.invoicesync.modules.stripe.model.UserBillingHistory;
import com.invoicesync.modules.stripe.model.UserDefaultCard;
import com.invoicesync.modules.stripe.service.StripeService;
import com.invoicesync.modules.subscription.guard.model.LimitResponseDTO;
import com.invoicesync.modules.subscription.mapper.UserSubscriptionMapper;
import com.invoicesync.modules.subscription.model.UserSubscription;
import com.invoicesync.modules.subscription.model.UserSubscriptionResponseDTO;
import com.invoicesync.modules.subscription.repository.UserSubscriptionRepository;
import com.invoicesync.modules.user.model.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceCollection;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionSchedule;
import com.stripe.model.checkout.Session;
import com.stripe.param.InvoiceListParams;
import com.stripe.param.SubscriptionScheduleUpdateParams;
import com.stripe.param.SubscriptionUpdateParams;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

  private final StripeMapper stripeMapper;

  private final StripeConfig priceConfig;

  private StripeService stripeService;

  private UserSubscriptionRepository userSubscriptionRepository;

  private UserSubscriptionMapper userSubscriptionMapper;

  @Override
  public UserSubscriptionResponseDTO getSubscriptionPlanByUserId(final Authentication connectedUser) {
    final String userId = connectedUser.getName();

    final Optional<UserSubscription> activeSubscriptionOpt =
        userSubscriptionRepository.findByCreatedByAndSubscriptionActive(userId, true);

    final UserSubscription subscription = activeSubscriptionOpt.orElseGet(this::buildEmptySubscription);

    final SubscriptionPlan plan = subscription.getSubscriptionPlan() != null
        ? subscription.getSubscriptionPlan()
        : NONE;

    return new UserSubscriptionResponseDTO(
        subscription.getCreatedBy(),
        subscription.getSubscriptionPlan() != null ? subscription.getSubscriptionPlan().name() : null,
        Boolean.TRUE.equals(subscription.getSubscriptionActive()),
        subscription.getStartDate(),
        subscription.getEndDate(),
        subscription.getMonthlyUsedReceiptExport(),
        subscription.getSubscriptionPrice(),
        plan.getFeatures()
    );
  }

  @Override
  public void createFreeSubscriptionForUser(final Authentication connectedUser) {
    final UserSubscription subscription = userSubscriptionMapper.toFreeSubscription();
    userSubscriptionRepository.save(subscription);
  }

  @Override
  public LimitResponseDTO getUserLimits(final Authentication connectedUser) {
    final UserSubscription subscription = userSubscriptionRepository
        .findByCreatedByAndSubscriptionActive(connectedUser.getName(), true)
        .orElseGet(() -> {
          log.info("[SUBSCRIPTION] No active subscription for userId={}, returning zero limits",
              connectedUser.getName());
          return buildEmptySubscription();
        });

    final int totalLimit = subscription.getMonthlyInvoiceExportLimit() + subscription.getMonthlyInvoiceCreateLimit()
        + subscription.getMonthlyReceiptExportLimit();
    final int usedLimit =
        subscription.getMonthlyUsedInvoiceExport() + subscription.getMonthlyUsedInvoiceCreate()
            + subscription.getMonthlyUsedReceiptExport();

    final int invoiceExportLimit = subscription.getMonthlyInvoiceExportLimit();
    final int invoiceCreateLimit = subscription.getMonthlyInvoiceCreateLimit();
    final int receiptExportLimit = subscription.getMonthlyReceiptExportLimit();

    final int invoiceExportUsedLimit = subscription.getMonthlyUsedInvoiceExport();
    final int invoiceCreateUsedLimit = subscription.getMonthlyUsedInvoiceCreate();
    final int receiptExportUsedLimit = subscription.getMonthlyUsedReceiptExport();

    return new LimitResponseDTO(
        usedLimit,
        totalLimit,
        invoiceExportLimit,
        invoiceExportUsedLimit,
        receiptExportLimit,
        receiptExportUsedLimit,
        invoiceCreateLimit,
        invoiceCreateUsedLimit
    );
  }

  @Override
  public void setEnterpriseSubscriptionForUser(final User user) {
    final UserSubscription subscription = userSubscriptionRepository.findByCreatedBy(String.valueOf(user.getId()))
        .orElseThrow(() -> {
          log.warn("[SUBSCRIPTION] Cannot set enterprise plan: no subscription record exists for userId={}",
              user.getId());
          return new NoActiveSubscriptionException("Subscription not found for user: " + user.getId());
        });

    subscription.setSubscriptionPlan(SubscriptionPlan.ENTERPRISE);
    subscription.setSubscriptionActive(true);
    subscription.setStartDate(LocalDateTime.now());
    subscription.setEndDate(LocalDateTime.now().plusMonths(1));
    subscription.setSubscriptionPrice(BigDecimal.valueOf(SubscriptionPlan.ENTERPRISE.getMonthlyPrice()));
    subscription.setMonthlyInvoiceExportLimit(SubscriptionPlan.ENTERPRISE.getMonthlyInvoiceExportLimit());
    subscription.setMonthlyInvoiceCreateLimit(SubscriptionPlan.ENTERPRISE.getMonthlyInvoiceCreateLimit());
    subscription.setMonthlyReceiptExportLimit(SubscriptionPlan.ENTERPRISE.getMonthlyReceiptExportLimit());
    userSubscriptionRepository.save(subscription);
  }

  @Override
  public void cancelUserSubscription(final Authentication connectedUser) throws StripeException {
    final UserSubscription userSubscription = userSubscriptionRepository
        .findByCreatedByAndSubscriptionActive(connectedUser.getName(), true)
        .orElseThrow(() -> {
          log.info("[SUBSCRIPTION] Cancel requested but no active subscription for userId={}",
              connectedUser.getName());
          return new NoActiveSubscriptionException(
              "Active subscription not found for user: " + connectedUser.getName());
        });

    final String stripeSubscriptionId = userSubscription.getStripeSubscriptionId();

    if (stripeSubscriptionId == null) {
      log.info("[SUBSCRIPTION] Cancel requested but no Stripe subscription is linked for userId={}",
          connectedUser.getName());
      throw new NoActiveSubscriptionException("Cannot cancel: no Stripe subscription is linked to this plan.");
    }

    final Subscription stripeSubscription = Subscription.retrieve(stripeSubscriptionId);

    // Replaces the schedule's phases with just the current one (dropping any pending downgrade)
    // instead of only setting end_behavior=CANCEL — the original phase 2 has no end_date, so
    // end_behavior would never actually be evaluated and the subscription would silently continue
    // onto the new plan. No direct Subscription.update() here either — Stripe rejects that while
    // a schedule manages the subscription (InvalidRequestException: "update the schedule instead").
    if (userSubscription.getStripeScheduleId() != null) {

      final SubscriptionSchedule existingSchedule =
          SubscriptionSchedule.retrieve(userSubscription.getStripeScheduleId());

      final String currentPriceId = priceConfig.getPriceIdForPlan(userSubscription.getSubscriptionPlan());
      final long currentPeriodStart = stripeSubscription.getItems().getData().getFirst().getCurrentPeriodStart();
      final long currentPeriodEnd = stripeSubscription.getItems().getData().getFirst().getCurrentPeriodEnd();

      final SubscriptionScheduleUpdateParams params =
          SubscriptionScheduleUpdateParams.builder()
              .addPhase(SubscriptionScheduleUpdateParams.Phase.builder()
                  .addItem(SubscriptionScheduleUpdateParams.Phase.Item.builder()
                      .setPrice(currentPriceId)
                      .setQuantity(1L)
                      .build())
                  .setStartDate(currentPeriodStart)
                  .setEndDate(currentPeriodEnd)
                  .build())
              .setEndBehavior(SubscriptionScheduleUpdateParams.EndBehavior.CANCEL)
              .build();

      existingSchedule.update(params);
    } else {
      final SubscriptionUpdateParams params =
          SubscriptionUpdateParams.builder().setCancelAtPeriodEnd(true).build();

      stripeSubscription.update(params);
    }

  }

  /**
   * Handles both "start a subscription" and "change plan" requests. Picks one of two Stripe
   * mechanisms depending on whether the user already has a real Stripe subscription to work with:
   *
   * <ul>
   *   <li><b>No active row, or an active row with no {@code stripeSubscriptionId}</b> (FREE —
   *       {@link UserSubscriptionMapper#toFreeSubscription()} never sets it — or any other row
   *       missing Stripe linkage, e.g. a missed webhook) → new Stripe Checkout Session. There is
   *       nothing on the Stripe side to update, so Stripe has to collect the card and create the
   *       customer/subscription from scratch.</li>
   *   <li><b>Active row with a {@code stripeSubscriptionId}</b> (normal paid → paid change) →
   *       direct {@link StripeService#upgradeSubscription} call (Stripe {@code Subscription.update}),
   *       no Checkout involved.</li>
   * </ul>
   *
   * <p>Note this method never deactivates the old row itself, even in the Checkout branch —
   * that intentionally waits until payment is actually confirmed, so an abandoned/failed Checkout
   * doesn't leave the user with no active row at all. The old row gets deactivated in
   * {@code StripeServiceImpl.handleSubscriptionPayment}'s "new subscription" branch instead,
   * atomically with creating the new one, once Stripe confirms the invoice was paid.</p>
   */
  @Override
  public Map<String, Object> startOrUpdateSubscription(final String planName, final Authentication connectedUser) {
    final String userId = connectedUser.getName();
    final SubscriptionPlan newPlan = SubscriptionPlan.valueOf(planName.toUpperCase());
    final String newPriceId = priceConfig.getPriceIdForPlan(newPlan);

    final Optional<UserSubscription> existingSubOpt =
        userSubscriptionRepository.findByCreatedByAndSubscriptionActive(userId, true);

    // No active subscription → new checkout
    if (existingSubOpt.isEmpty()) {
      final Session session = stripeService.createCheckoutSession(userId, newPriceId);
      return Map.of("id", session.getId());
    }

    final UserSubscription existingSub = existingSubOpt.get();
    final String stripeSubscriptionId = existingSub.getStripeSubscriptionId();

    // No Stripe subscription to update (FREE, or paid but not yet linked) → go through checkout
    if (stripeSubscriptionId == null) {
      final Session session = stripeService.createCheckoutSession(userId, newPriceId);
      return Map.of("id", session.getId());
    }

    // Real Stripe subscription already exists → update it directly via the Stripe API
    stripeService.upgradeSubscription(existingSub, newPriceId, newPlan);
    return Map.of("upgraded", true);
  }

  @Override
  public UserDefaultCard getUserDefaultCard(final Authentication connectedUser) {

    final UserSubscription subscription = userSubscriptionRepository
        .findByCreatedByAndSubscriptionActive(connectedUser.getName(), true)
        .orElseGet(() -> {
          log.info("[SUBSCRIPTION] No active subscription for userId={}, returning empty default card",
              connectedUser.getName());
          return buildEmptySubscription();
        });

    final String stripeSubscriptionId = subscription.getStripeSubscriptionId();

    if (stripeSubscriptionId == null) {
      return new UserDefaultCard("", "", 0L, 0L);
    }

    try {
      final Subscription stripeSubscription = Subscription.retrieve(stripeSubscriptionId);
      final String paymentMethodId = stripeSubscription.getDefaultPaymentMethod();

      if (paymentMethodId == null) {
        return new UserDefaultCard("", "", 0L, 0L);
      }

      final PaymentMethod userDefaultPayment = PaymentMethod.retrieve(paymentMethodId);

      return stripeMapper.toUserDefaultCard(userDefaultPayment);
    } catch (StripeException e) {
      return new UserDefaultCard("", "", 0L, 0L);
    }
  }

  @Override
  public List<UserBillingHistory> getUserBillingHistory(final Authentication connectedUser) {
    final UserSubscription subscription = userSubscriptionRepository
        .findByCreatedByAndSubscriptionActive(connectedUser.getName(), true)
        .orElseGet(() -> {
          log.info("[SUBSCRIPTION] No active subscription for userId={}, returning empty billing history",
              connectedUser.getName());
          return buildEmptySubscription();
        });

    try {
      final String stripeCustomerId = subscription.getStripeCustomerId();

      if (stripeCustomerId == null) {
        return List.of();
      }

      InvoiceListParams params = InvoiceListParams.builder().setCustomer(stripeCustomerId).setLimit(10L).build();

      final InvoiceCollection userInvoices = Invoice.list(params);

      return userInvoices.getData().stream()
          .map(stripeMapper::toUserBillingHistory)
          .toList();
    } catch (StripeException e) {
      return List.of();
    }

  }

  private UserSubscription buildEmptySubscription() {
    final UserSubscription empty = new UserSubscription();
    empty.setSubscriptionPlan(NONE);
    empty.setSubscriptionActive(false);
    empty.setStartDate(null);
    empty.setEndDate(null);
    empty.setSubscriptionPrice(BigDecimal.ZERO);
    empty.setMonthlyInvoiceExportLimit(NONE.getMonthlyInvoiceExportLimit());
    empty.setMonthlyInvoiceCreateLimit(NONE.getMonthlyInvoiceCreateLimit());
    empty.setMonthlyReceiptExportLimit(NONE.getMonthlyReceiptExportLimit());
    empty.setMonthlyUsedInvoiceExport(0);
    empty.setMonthlyUsedInvoiceCreate(0);
    empty.setMonthlyUsedReceiptExport(0);
    return empty;
  }

}
