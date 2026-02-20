package com.invoicesync.modules.subscription.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.modules.stripe.service.StripeService;
import com.invoicesync.modules.subscription.guard.model.LimitResponseDTO;
import com.invoicesync.modules.subscription.model.UserSubscriptionResponseDTO;
import com.invoicesync.modules.subscription.service.UserSubscriptionService;
import com.stripe.exception.StripeException;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(Api.SUBSCRIPTION)
public class UserSubscriptionController {

  private final UserSubscriptionService userSubscriptionService;

  private final StripeService stripeService;

  @GetMapping(Api.SUBSCRIPTION_GET_USER_PLAN)
  public ResponseEntity<UserSubscriptionResponseDTO> getUserPlan(final Authentication connectedUser) {
    return ResponseEntity.ok(userSubscriptionService.getSubscriptionPlanByUserId(connectedUser));
  }

  @PostMapping(Api.SUBSCRIPTION_SUBSCRIBE)
  public ResponseEntity<Map<String, Object>> subscribeToPlan(
      @RequestBody final Map<String, String> request,
      final Authentication connectedUser
  ) throws StripeException {

    final String planName = request.get("plan");

    if (planName == null) {
      return ResponseEntity.badRequest().body(Map.of("error", "Missing plan"));
    }

    if ("FREE".equalsIgnoreCase(planName)) {
      userSubscriptionService.createFreeSubscriptionForUser(connectedUser);
      return ResponseEntity.ok(Map.of("message", "FREE plan activated"));
    }

    try {
      final Map<String, Object> response = userSubscriptionService.startOrUpdateSubscription(planName, connectedUser);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping(Api.SUBSCRIPTION_GET_USER_LIMIT)
  public LimitResponseDTO getUserLimits(final Authentication connectedUser) {
    return userSubscriptionService.getUserLimits(connectedUser);
  }

  @PostMapping(Api.SUBSCRIPTION_CANCEL)
  public ResponseEntity<Map<String, String>> cancelSubscription(final Authentication connectedUser)
      throws StripeException {
    userSubscriptionService.cancelUserSubscription(connectedUser);
    return ResponseEntity.ok(Map.of("message", "Subscription cancelled successfully"));
  }

}
