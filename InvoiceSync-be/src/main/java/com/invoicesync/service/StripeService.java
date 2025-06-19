package com.invoicesync.service;

import java.util.Map;

import org.springframework.security.core.Authentication;

import com.stripe.exception.StripeException;

public interface StripeService {

  Map<String, Object> createCheckoutSession(String plan, Authentication connectedUser)
      throws StripeException;
}
