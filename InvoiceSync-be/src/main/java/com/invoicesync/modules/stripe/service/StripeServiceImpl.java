package com.invoicesync.modules.stripe.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicesync.config.StripeConfig;
import com.invoicesync.core.enums.SubscriptionPlan;
import com.invoicesync.modules.stripe.repository.StripeProcessedEventRepository;
import com.invoicesync.modules.subscription.mapper.UserSubscriptionMapper;
import com.invoicesync.modules.subscription.model.UserSubscription;
import com.invoicesync.modules.subscription.repository.UserSubscriptionRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

  private final UserSubscriptionRepository userSubscriptionRepository;

  private final UserSubscriptionMapper userSubscriptionMapper;

  private final StripeProcessedEventRepository stripeProcessedEventRepository;

  private final StripeConfig stripeConfig;

  @Value("${app.frontend.url}")
  private String frontendUrl;

  @Override
  @Transactional
  public boolean tryMarkEventProcessed(final String eventId) {
    return stripeProcessedEventRepository.tryInsert(eventId) == 1;
  }

  // TODO move this to the subscription service
  @Override
  public void handleSubscriptionCanceled(final Event event) throws StripeException {
    final Subscription stripeSubscription = (Subscription) event.getDataObjectDeserializer()
        .getObject().orElseThrow();

    final String subscriptionId = stripeSubscription.getId();
    final UserSubscription userSubscription = userSubscriptionRepository
        .findByStripeSubscriptionId(subscriptionId);

    if (userSubscription != null) {
      userSubscription.setSubscriptionActive(false);
      userSubscriptionRepository.save(userSubscription);
    }
  }

  @Override
  public void handleSubscriptionPaymentFailed(final Event event) throws StripeException {
    final Invoice stripeInvoice = (Invoice) event.getDataObjectDeserializer().getObject().orElseThrow();
    final String stripeSubscriptionId = stripeInvoice.getParent().getSubscriptionDetails().getSubscription();
    final UserSubscription userSubscription =
        userSubscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId);

    if (userSubscription != null) {
      userSubscription.setSubscriptionActive(false);
      userSubscriptionRepository.save(userSubscription);
    }
  }

  /**
   * Handles {@code invoice.payment_succeeded}. Looks up the local row by
   * {@code stripeSubscriptionId} to tell apart two cases:
   *
   * <ul>
   *   <li><b>Row found</b> (renewal, or a direct paid → paid change made via
   *       {@link #upgradeSubscription}): plan/price/limits are updated unconditionally — a plan
   *       change must take effect on the invoice that reflects it, whether or not it's a real
   *       renewal. Usage counters and start/end dates are reset only when
   *       {@code billingReason == "subscription_cycle"} (an actual renewal), not on a mid-cycle
   *       proration invoice from a plan change — otherwise switching plans back and forth could
   *       be used to reset usage limits for free.</li>
   *   <li><b>No row found</b> (first payment for a brand-new Stripe subscription, e.g. FREE → paid
   *       via Checkout): deactivates whatever row is currently active for this user (FREE, or a
   *       paid row with a missing Stripe link) and creates a fresh one from the invoice. This is
   *       done here — atomically with creating the new row — rather than eagerly in
   *       {@code UserSubscriptionServiceImpl.startOrUpdateSubscription}, precisely so an
   *       abandoned/failed Checkout never leaves the user with zero active rows.</li>
   * </ul>
   */
  // TODO move this to the subscription service
  @Override
  public void handleSubscriptionPayment(final Event event) throws StripeException {
    final Invoice stripeInvoice = (Invoice) event.getDataObjectDeserializer().getObject().orElseThrow();
    final var line = stripeInvoice.getLines().getData().getFirst();
    final String userIdStr = line.getMetadata().get("userId");

    final String stripeSubscriptionId = stripeInvoice.getParent().getSubscriptionDetails().getSubscription();
    final Subscription stripeSubscription = Subscription.retrieve(stripeSubscriptionId);
    final String priceId = stripeSubscription.getItems().getData().getFirst().getPrice().getId();
    final SubscriptionPlan newPlan = stripeConfig.getPlanForPriceId(priceId);

    final UserSubscription userSubscription =
        userSubscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId);

    if (userSubscription != null) {

      userSubscription.setSubscriptionPlan(newPlan);
      userSubscription.setSubscriptionPrice(BigDecimal.valueOf(newPlan.getMonthlyPrice()));
      userSubscription.setMonthlyInvoiceCreateLimit(newPlan.getMonthlyInvoiceCreateLimit());
      userSubscription.setMonthlyInvoiceExportLimit(newPlan.getMonthlyInvoiceExportLimit());
      userSubscription.setMonthlyReceiptExportLimit(newPlan.getMonthlyReceiptExportLimit());

      if ("subscription_cycle".equals(stripeInvoice.getBillingReason())) {
        final LocalDateTime start =
            LocalDateTime.ofInstant(Instant.ofEpochSecond(line.getPeriod().getStart()), ZoneId.systemDefault());
        final LocalDateTime end =
            LocalDateTime.ofInstant(Instant.ofEpochSecond(line.getPeriod().getEnd()), ZoneId.systemDefault());

        userSubscription.setStartDate(start);
        userSubscription.setEndDate(end);
        userSubscription.setMonthlyUsedInvoiceExport(0);
        userSubscription.setMonthlyUsedInvoiceCreate(0);
        userSubscription.setMonthlyUsedReceiptExport(0);
      }

      userSubscription.setSubscriptionActive(true);
      userSubscriptionRepository.save(userSubscription);
    } else {
      // New subscription: create from invoice data
      userSubscriptionRepository.findByCreatedByAndSubscriptionActive(userIdStr, true)
          .ifPresent(old -> {
            old.setSubscriptionActive(false);
            userSubscriptionRepository.save(old);
          });

      final UserSubscription subscription = userSubscriptionMapper.fromStripeInvoice(stripeInvoice, userIdStr);
      userSubscriptionRepository.save(subscription);
    }
  }

  @Override
  public void upgradeSubscription(final UserSubscription currentPlan, final String newPriceId, final
  SubscriptionPlan newPlan) {
    {
      final String stripeSubscriptionId = currentPlan.getStripeSubscriptionId();
      final String stripeSubscriptionItemId = currentPlan.getStripeSubscriptionItemId();
      final SubscriptionUpdateParams.ProrationBehavior updateSubMode = (newPlan.getMonthlyPrice()
          > currentPlan.getSubscriptionPlan().getMonthlyPrice())
          ? SubscriptionUpdateParams.ProrationBehavior.ALWAYS_INVOICE
          : SubscriptionUpdateParams.ProrationBehavior.CREATE_PRORATIONS;

      try {
        // Retrieve a subscription from Stripe
        Subscription subscription = null;

        subscription = Subscription.retrieve(stripeSubscriptionId);

        // Update subscription to a new plan → immediately invoice the difference
        final SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
            .addItem(SubscriptionUpdateParams.Item.builder()
                .setId(stripeSubscriptionItemId)
                .setPrice(newPriceId)
                .build())
            .setProrationBehavior(updateSubMode)
            .build();

        subscription = subscription.update(params);
      } catch (StripeException e) {
        throw new RuntimeException(e);
      }
    }

  }

  @Override
  public Session createCheckoutSession(final String userId, final String newPriceId) {
    try {
      final SessionCreateParams params = SessionCreateParams.builder()
          .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
          .addLineItem(SessionCreateParams.LineItem.builder()
              .setPrice(newPriceId)
              .setQuantity(1L)
              .build())
          .setSubscriptionData(
              SessionCreateParams.SubscriptionData.builder()
                  .putMetadata("userId", userId)
                  .build()
          )
          .setSuccessUrl(frontendUrl + "/web/subscription")
          .setCancelUrl(frontendUrl + "/home")
          .setPaymentMethodOptions(
              SessionCreateParams.PaymentMethodOptions.builder()
                  .setCard(SessionCreateParams.PaymentMethodOptions.Card.builder()
                      .setRequestThreeDSecure(
                          SessionCreateParams.PaymentMethodOptions.Card.RequestThreeDSecure.AUTOMATIC)
                      .build())
                  .build())
          .build();
      return Session.create(params);
    } catch (StripeException e) {
      throw new RuntimeException(e);
    }
  }

}
