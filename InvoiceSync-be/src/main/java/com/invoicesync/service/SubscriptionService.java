package com.invoicesync.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.dto.subscription.LimitResponseDTO;
import com.invoicesync.dto.subscription.SubscriptionResponseDTO;

public interface SubscriptionService {

  SubscriptionResponseDTO getSubscriptionPlanByUserId(Authentication connectedUser);

  void createFreeSubscriptionForUser(Authentication connectedUser);

  LimitResponseDTO getUserLimits(Authentication connectedUser);

}
