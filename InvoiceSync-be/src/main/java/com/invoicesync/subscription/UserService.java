package com.invoicesync.subscription;

import org.springframework.security.core.Authentication;

import com.invoicesync.subscription.enums.LimitType;

public interface UserService {

  /**
   * Retrieves the configured limit for a specific type of action for the connected user.
   *
   * @param connectedUser currently authenticated user
   * @param limitType type of limit to retrieve (e.g., invoice export, receipt export)
   * @return configured limit as an integer
   */
  int getLimit(Authentication connectedUser, LimitType limitType);

  /**
   * Retrieves the current usage of a specific type of action for the connected user.
   *
   * @param connectedUser currently authenticated user
   * @param limitType type of limit to check usage for
   * @return number of actions already used
   */
  int getUsed(Authentication connectedUser, LimitType limitType);

  /**
   * Increments the usage count for a specific type of action for the connected user.
   *
   * @param connectedUser currently authenticated user
   * @param limitType type of limit to increment
   */
  void incrementUsed(Authentication connectedUser, LimitType limitType);

}
