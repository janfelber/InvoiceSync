package com.invoicesync.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.dto.subscription.LimitResponseDTO;
import com.invoicesync.dto.subscription.SubscriptionResponseDTO;
import com.invoicesync.service.SubscriptionService;
import com.invoicesync.user.CurrentUserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  private final CurrentUserService currentUserService;

  @GetMapping("/user/plan")
  public SubscriptionResponseDTO getUserPlan() {
    final Long userId = currentUserService.getCurrentUserId();
    return subscriptionService.getSubscriptionPlanByUserId(userId);
  }

  @GetMapping("/user/limit")
  public LimitResponseDTO getUserLimits() {
    final Long userId = currentUserService.getCurrentUserId();
    return subscriptionService.getUserLimits(userId);
  }

}
