package com.invoicesync.subscription;

import org.springframework.security.core.Authentication;

import com.invoicesync.subscription.dto.SubscriptionResponseDTO;
import com.invoicesync.subscription.guard.LimitResponseDTO;

public interface SubscriptionService {

  SubscriptionResponseDTO getSubscriptionPlanByUserId(Authentication connectedUser);

  void createFreeSubscriptionForUser(Authentication connectedUser);

  LimitResponseDTO getUserLimits(Authentication connectedUser);

}
