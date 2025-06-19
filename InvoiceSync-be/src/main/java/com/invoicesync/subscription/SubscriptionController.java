package com.invoicesync.subscription;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.stripe.StripeService;
import com.invoicesync.subscription.dto.SubscriptionResponseDTO;
import com.invoicesync.subscription.guard.LimitResponseDTO;
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
  public ResponseEntity<Map<String, Object>> createCheckout(@RequestBody final Map<String, String> request,
      final Authentication connectedUser)
      throws StripeException {
    final String planName = request.get("plan");

    if ("FREE".equals(planName)) {
      subscriptionService.createFreeSubscriptionForUser(connectedUser);
      return ResponseEntity.ok(Map.of("message", "FREE plan activated"));
    }

    try {
      final Map<String, Object> response = stripeService.createCheckoutSession(planName, connectedUser);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping("/user/limit")
  public LimitResponseDTO getUserLimits(final Authentication connectedUser) {
    return subscriptionService.getUserLimits(connectedUser);
  }

}
