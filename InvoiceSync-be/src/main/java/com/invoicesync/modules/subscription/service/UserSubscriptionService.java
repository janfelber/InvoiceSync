package com.invoicesync.modules.subscription.service;

import java.util.Map;

import org.springframework.security.core.Authentication;

import com.invoicesync.modules.subscription.guard.model.LimitResponseDTO;
import com.invoicesync.modules.subscription.model.UserSubscriptionResponseDTO;
import com.invoicesync.modules.user.model.User;
import com.stripe.exception.StripeException;

public interface UserSubscriptionService {

  /**
   * Retrieves the current subscription plan of the connected user.
   *
   * @param connectedUser currently authenticated user
   * @return subscription plan details as a DTO
   */
  UserSubscriptionResponseDTO getSubscriptionPlanByUserId(Authentication connectedUser);

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

  void cancelUserSubscription(Authentication connectedUser) throws StripeException;

  Map<String, Object> startOrUpdateSubscription(String planName, Authentication connectedUser);

}
