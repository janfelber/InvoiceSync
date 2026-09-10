package com.invoicesync.modules.stripe.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.modules.stripe.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class StripeController {

  private final StripeService stripeService;

  @Value("${stripe.webhook.secret}")
  private String stripeWebhookSecret;

  public StripeController(final StripeService stripeService) {
    this.stripeService = stripeService;
  }

  @PostMapping(Api.STRIPE_WEBHOOK)
  public ResponseEntity<String> handleStripeWebhook(
      @RequestBody final String payload,
      @RequestHeader("Stripe-Signature") final String sigHeader
  ) throws StripeException {

    final Event event;
    try {
      event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
    } catch (Exception e) {
      log.error("[STRIPE] Webhook signature verification failed", e);
      return ResponseEntity.badRequest().body("Invalid signature");
    }

    if (!stripeService.tryMarkEventProcessed(event.getId())) {
      return ResponseEntity.ok("Event already processed");
    }

    switch (event.getType()) {
      case "invoice.payment_succeeded":
        stripeService.handleSubscriptionPayment(event);
        break;
      case "invoice.payment_failed":
        stripeService.handleSubscriptionPaymentFailed(event);
        break;
      case "customer.subscription.deleted":
        stripeService.handleSubscriptionCanceled(event);
        break;
      default:
        break;
    }

    return ResponseEntity.ok("Webhook processed");
  }

}
