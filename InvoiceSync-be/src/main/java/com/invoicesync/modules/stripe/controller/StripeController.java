package com.invoicesync.modules.stripe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.modules.stripe.service.StripeService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class StripeController {

  private StripeService stripeService;
  private static final String STRIPE_WEBHOOK_SECRET = "whsec_P8k8tcVJUsoepfujhP4nVloiuBnX4hVr";

  @PostMapping("/stripe/webhook")
  public ResponseEntity<String> handleStripeWebhook(@RequestBody final String payload,
      @RequestHeader("Stripe-Signature") final String sigHeader) {
    final Event event;

    try {
      event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Invalid signature");
    }

    if ("invoice.payment_succeeded".equals(event.getType())) {
      stripeService.handleSubscriptionPayment(event);
    }

    return ResponseEntity.ok("Webhook processed");
  }

}
