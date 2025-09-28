package com.invoicesync.subscription;

import org.springframework.security.core.Authentication;

import com.invoicesync.subscription.dto.SubscriptionResponseDTO;
import com.invoicesync.subscription.guard.LimitResponseDTO;

public interface SubscriptionService {

  /**
   * Retrieves the current subscription plan of the connected user.
   *
   * @param connectedUser currently authenticated user
   * @return subscription plan details as a DTO
   */
  SubscriptionResponseDTO getSubscriptionPlanByUserId(Authentication connectedUser);

  /**
   * Creates a free subscription plan for the connected user.
   *
   * @param connectedUser currently authenticated user
   */
  void createFreeSubscriptionForUser(Authentication connectedUser);

  /**
   * Retrieves the usage limits of the connected user.
   *
   * @param connectedUser currently authenticated user
   * @return user's limits as a DTO
   */
  LimitResponseDTO getUserLimits(Authentication connectedUser);

}
