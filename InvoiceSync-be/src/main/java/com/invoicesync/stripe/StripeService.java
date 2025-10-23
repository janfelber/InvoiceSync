package com.invoicesync.stripe;

import java.util.Map;

import org.springframework.security.core.Authentication;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;

public interface StripeService {

  Map<String, Object> createCheckoutSession(String plan, Authentication connectedUser)
      throws StripeException;

  void handleSubscriptionPayment(Event event);
}
