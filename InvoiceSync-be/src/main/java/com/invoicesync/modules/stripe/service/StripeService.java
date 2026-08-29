package com.invoicesync.modules.stripe.service;

import com.invoicesync.modules.subscription.model.UserSubscription;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

public interface StripeService {

  boolean tryMarkEventProcessed(String eventId);

  void handleSubscriptionPayment(Event event) throws StripeException;

  void handleSubscriptionCanceled(Event event) throws StripeException;

  Session createCheckoutSession(String userId, String newPlanPriceId);

  void upgradeSubscription(UserSubscription subscriptionId, String newPlanPriceId);

}
