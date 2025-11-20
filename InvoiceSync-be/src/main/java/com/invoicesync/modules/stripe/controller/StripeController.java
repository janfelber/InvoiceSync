package com.invoicesync.modules.stripe.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.modules.stripe.service.StripeService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@RestController
public class StripeController {

  private final StripeService stripeService;

  @Value("${stripe.webhook.secret}")
  private String stripeWebhookSecret;

  public StripeController(final StripeService stripeService) {
    this.stripeService = stripeService;
  }

  @PostMapping("/stripe/webhook")
  public ResponseEntity<String> handleStripeWebhook(@RequestBody final String payload,
      @RequestHeader("Stripe-Signature") final String sigHeader) {
    final Event event;
    try {
      event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Invalid signature");
    }

    if ("invoice.payment_succeeded".equals(event.getType())) {
      stripeService.handleSubscriptionPayment(event);
    }

    return ResponseEntity.ok("Webhook processed");
  }

}
