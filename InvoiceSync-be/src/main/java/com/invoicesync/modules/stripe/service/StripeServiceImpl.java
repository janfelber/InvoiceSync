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
import com.stripe.model.SubscriptionSchedule;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionScheduleCreateParams;
import com.stripe.param.SubscriptionScheduleUpdateParams;
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
    // Read the price from the subscription's current item, NOT the invoice line: a proration
    // invoice (upgrade/downgrade) has 2 line items (old-plan credit + new-plan charge) in
    // unspecified order, so line.getFirst() sometimes picked the credit line and reverted the
    // stored plan by one step. The subscription always has exactly 1 item, so no ambiguity.
    final String priceId = stripeSubscription.getItems().getData().getFirst().getPrice().getId();
    final SubscriptionPlan newPlan = stripeConfig.getPlanForPriceId(priceId);

    final UserSubscription userSubscription =
        userSubscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId);

    if (userSubscription != null) {

      userSubscription.setSubscriptionPlan(newPlan);
      userSubscription.setSubscriptionPrice(BigDecimal.valueOf(newPlan.getMonthlyPrice()));
      // Refreshed on every payment, not just once: a Schedule phase transition issues a NEW
      // subscription item id — it does not reuse the old one — so the stored id goes stale after
      // any downgrade and the next direct upgrade fails with "No subscription item with this ID".
      userSubscription.setStripeSubscriptionItemId(
          stripeSubscription.getItems().getData().getFirst().getId());
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

      try {
        // Retrieve a subscription from Stripe
        final Subscription subscription = Subscription.retrieve(stripeSubscriptionId);
        // Downgrade goes through a Schedule instead of an immediate price change + proration.
        // Deliberate: the customer keeps the paid-for (more expensive) plan until period end, then
        // switches with no credit — an immediate CREATE_PRORATIONS change banks a credit for the
        // unused expensive time that silently covers several future invoices for free.
        if (newPlan.getMonthlyPrice() < currentPlan.getSubscriptionPlan().getMonthlyPrice()) {
          final String currentPriceId = stripeConfig.getPriceIdForPlan(currentPlan.getSubscriptionPlan());

          if (currentPlan.getStripeScheduleId() == null) {
            final SubscriptionScheduleCreateParams createParams = SubscriptionScheduleCreateParams.builder()
                .setFromSubscription(currentPlan.getStripeSubscriptionId())
                .build();

            final SubscriptionSchedule schedule = SubscriptionSchedule.create(createParams);

            applyDowngradePhases(currentPriceId, newPriceId, subscription, schedule);
            currentPlan.setStripeScheduleId(schedule.getId());
            userSubscriptionRepository.save(currentPlan);
          } else {
            final SubscriptionSchedule existingSchedule =
                SubscriptionSchedule.retrieve(currentPlan.getStripeScheduleId());

            applyDowngradePhases(currentPriceId, newPriceId, subscription, existingSchedule);
          }

        } else {
          // A pending downgrade schedule must be released before a direct update — Stripe rejects
          // Subscription.update() on anything a schedule manages. release() (not cancel()) leaves
          // the subscription running normally on its current, not-yet-transitioned phase.
          if (currentPlan.getStripeScheduleId() != null) {
            final SubscriptionSchedule existingSchedule =
                SubscriptionSchedule.retrieve(currentPlan.getStripeScheduleId());
            existingSchedule.release();

            currentPlan.setStripeScheduleId(null);
            userSubscriptionRepository.save(currentPlan);
          }

          final String stripeSubscriptionItemId = currentPlan.getStripeSubscriptionItemId();

          // Update subscription to a new plan → immediately invoice the difference
          final SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
              .addItem(SubscriptionUpdateParams.Item.builder()
                  .setId(stripeSubscriptionItemId)
                  .setPrice(newPriceId)
                  .build())
              .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.ALWAYS_INVOICE)
              .build();

          subscription.update(params);
        }
      } catch (StripeException e) {
        throw new RuntimeException(e);
      }
    }

  }

  private void applyDowngradePhases(final String currentPriceId, final String newPriceId,
      final Subscription subscription, final SubscriptionSchedule schedule) throws StripeException {

    long currentPeriodStart = subscription.getItems().getData().getFirst().getCurrentPeriodStart();
    long currentPeriodEnd = subscription.getItems().getData().getFirst().getCurrentPeriodEnd();

    final SubscriptionScheduleUpdateParams updateParams = SubscriptionScheduleUpdateParams.builder()
        .addPhase(SubscriptionScheduleUpdateParams.Phase.builder()
            .addItem(SubscriptionScheduleUpdateParams.Phase.Item.builder()
                .setPrice(currentPriceId)
                .setQuantity(1L)
                .build())
            .setStartDate(currentPeriodStart)
            .setEndDate(currentPeriodEnd)
            .build())
        // No end_date/iterations here on purpose: this is the last phase, so it continues
        // indefinitely once reached, instead of the schedule trying to end again.
        .addPhase(SubscriptionScheduleUpdateParams.Phase.builder()
            .addItem(SubscriptionScheduleUpdateParams.Phase.Item.builder()
                .setPrice(newPriceId)
                .setQuantity(1L)
                .build())
            .setProrationBehavior(SubscriptionScheduleUpdateParams.Phase.ProrationBehavior.NONE)
            .build())
        .build();

    schedule.update(updateParams);
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
