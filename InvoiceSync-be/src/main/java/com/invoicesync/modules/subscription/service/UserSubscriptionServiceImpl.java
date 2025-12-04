package com.invoicesync.modules.subscription.service;

import static com.invoicesync.core.enums.SubscriptionPlan.NONE;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.config.StripeConfig;
import com.invoicesync.core.enums.SubscriptionPlan;
import com.invoicesync.modules.stripe.service.StripeService;
import com.invoicesync.modules.subscription.guard.model.LimitResponseDTO;
import com.invoicesync.modules.subscription.mapper.UserSubscriptionMapper;
import com.invoicesync.modules.subscription.model.UserSubscription;
import com.invoicesync.modules.subscription.model.UserSubscriptionResponseDTO;
import com.invoicesync.modules.subscription.repository.UserSubscriptionRepository;
import com.invoicesync.modules.user.model.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionUpdateParams;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

  private StripeService stripeService;

  private UserSubscriptionRepository userSubscriptionRepository;

  private UserSubscriptionMapper userSubscriptionMapper;

  private final StripeConfig priceConfig;

  @Override
  public UserSubscriptionResponseDTO getSubscriptionPlanByUserId(final Authentication connectedUser) {
    final String userId = connectedUser.getName();

    final Optional<UserSubscription> activeSubscriptionOpt =
        userSubscriptionRepository.findByCreatedByAndSubscriptionActive(userId, true);

    final UserSubscription subscription = activeSubscriptionOpt.orElseGet(() -> {
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
    });

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
        .orElseThrow(() -> new IllegalStateException("Subscription not found for user: " + connectedUser.getName()));

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
        .orElseThrow(() -> new IllegalStateException("Subscription not found for user: " + user.getId()));

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
        .orElseThrow(
            () -> new IllegalStateException("Active subscription not found for user: " + connectedUser.getName()));

    final String stripeSubscriptionId = userSubscription.getStripeSubscriptionId();

    final Subscription stripeSubscription = Subscription.retrieve(stripeSubscriptionId);

    final SubscriptionUpdateParams params =
        SubscriptionUpdateParams.builder().setCancelAtPeriodEnd(true).build();

    stripeSubscription.update(params);
  }

  @Override
  public Map<String, Object> startOrUpdateSubscription(final String planName, final Authentication connectedUser) {
    final String userId = connectedUser.getName();
    final SubscriptionPlan newPlan = SubscriptionPlan.valueOf(planName.toUpperCase());
    final String newPriceId = priceConfig.getPriceIdForPlan(newPlan);
    final Session session = stripeService.createCheckoutSession(userId, newPriceId);

    final Optional<UserSubscription> existingSubOpt =
        userSubscriptionRepository.findByCreatedByAndSubscriptionActive(userId, true);
    if (existingSubOpt.isEmpty()) {
      return Map.of("id", session.getId());
    }

    final UserSubscription existingSub = existingSubOpt.get();

    if (existingSubOpt.get().getSubscriptionPlan() == SubscriptionPlan.FREE) {
      existingSub.setSubscriptionActive(false);
      userSubscriptionRepository.save(existingSub);
      return Map.of("id", session.getId());
    }

    stripeService.upgradeSubscription(existingSub, newPriceId);

    existingSub.setSubscriptionPlan(newPlan);
    existingSub.setSubscriptionPrice(BigDecimal.valueOf(newPlan.getMonthlyPrice()));
    userSubscriptionRepository.save(existingSub);
    return Map.of("id", session.getId());
  }

}
