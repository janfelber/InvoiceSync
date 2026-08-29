package com.invoicesync.modules.stripe.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Value("${app.frontend.url}")
  private String frontendUrl;

  private final UserSubscriptionRepository userSubscriptionRepository;

  private final UserSubscriptionMapper userSubscriptionMapper;

  private final StripeProcessedEventRepository stripeProcessedEventRepository;

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

  // TODO move this to the subscription service
  @Override
  public void handleSubscriptionPayment(final Event event) throws StripeException {
    final Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElseThrow();
    final var line = invoice.getLines().getData().getFirst();
    final String userIdStr = line.getMetadata().get("userId");

    final String stripeSubscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();
    final UserSubscription existing = userSubscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId);

    if (existing != null) {
      // Renewal: update billing period and reset monthly usage counters
      final LocalDateTime start =
          LocalDateTime.ofInstant(Instant.ofEpochSecond(line.getPeriod().getStart()), ZoneId.systemDefault());
      final LocalDateTime end =
          LocalDateTime.ofInstant(Instant.ofEpochSecond(line.getPeriod().getEnd()), ZoneId.systemDefault());
      existing.setStartDate(start);
      existing.setEndDate(end);
      existing.setSubscriptionActive(true);
      existing.setMonthlyUsedInvoiceExport(0);
      existing.setMonthlyUsedInvoiceCreate(0);
      existing.setMonthlyUsedReceiptExport(0);
      userSubscriptionRepository.save(existing);
    } else {
      // New subscription: create from invoice data
      final UserSubscription subscription = userSubscriptionMapper.fromStripeInvoice(invoice, userIdStr);
      userSubscriptionRepository.save(subscription);
    }
  }

  @Override
  public void upgradeSubscription(final UserSubscription currentPlan, final String newPriceId) {
    {
      final String stripeSubscriptionId = currentPlan.getStripeSubscriptionId();
      final String stripeSubscriptionItemId = currentPlan.getStripeSubscriptionItemId();

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
            .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.ALWAYS_INVOICE)
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
