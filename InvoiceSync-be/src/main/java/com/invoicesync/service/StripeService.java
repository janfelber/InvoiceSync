package com.invoicesync.service;

import org.springframework.security.core.Authentication;

import com.stripe.exception.StripeException;

public interface StripeService {
  String createCheckoutSession(String priceId, Authentication connectedUser) throws StripeException;
}
