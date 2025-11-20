package com.invoicesync.modules.subscription.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.modules.subscription.guard.model.LimitResponseDTO;
import com.invoicesync.modules.subscription.model.SubscriptionResponseDTO;
import com.invoicesync.modules.user.model.User;

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

  void setEnterpriseSubscriptionForUser(User user);

}
