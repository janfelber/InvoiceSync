package com.invoicesync.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.dto.subscription.LimitResponseDTO;
import com.invoicesync.dto.subscription.SubscriptionResponseDTO;
import com.invoicesync.service.StripeService;
import com.invoicesync.service.SubscriptionService;
import com.invoicesync.subscription.SubscriptionPlan;
import com.stripe.exception.StripeException;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  private final StripeService stripeService;

  @GetMapping("/user/plan")
  public ResponseEntity<SubscriptionResponseDTO> getUserPlan(final Authentication connectedUser) {
    return ResponseEntity.ok(subscriptionService.getSubscriptionPlanByUserId(connectedUser));
  }

  @PostMapping("/subscribe")
  public ResponseEntity<Map<String, String>> createSubscription(@RequestBody final Map<String, String> request,
      final Authentication connectedUser) {
    final String planName = request.get("plan");
    final SubscriptionPlan plan;

    try {
      plan = SubscriptionPlan.valueOf(planName.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", "Invalid subscription plan"));
    }

    // Napr. FREE má null cenu, netreba Stripe
    if (plan == SubscriptionPlan.FREE) {
      subscriptionService.createFreeSubscriptionForUser(connectedUser);
      return ResponseEntity.ok(Map.of("message", "Free subscription activated"));
    }

    try {
      final String checkoutUrl = stripeService.createCheckoutSession(plan.getPriceId(), connectedUser);
      return ResponseEntity.ok(Map.of("url", checkoutUrl));
    } catch (StripeException e) {
      return ResponseEntity.status(500).body(Map.of("error", "Failed to create Stripe session"));
    }
  }

  @GetMapping("/user/limit")
  public LimitResponseDTO getUserLimits(final Authentication connectedUser) {
    return subscriptionService.getUserLimits(connectedUser);
  }

}
