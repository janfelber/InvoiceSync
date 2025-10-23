package com.invoicesync;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stripe.Stripe;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StripeInitializer {

  @Value("${stripe.scret.api.key}")
  private String stripeSecretKey;

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    Stripe.apiKey = stripeSecretKey;
    log.info("Stripe initialized!");
  }
}