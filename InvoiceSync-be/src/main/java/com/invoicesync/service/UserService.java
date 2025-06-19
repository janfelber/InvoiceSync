package com.invoicesync.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.subscription.LimitType;

public interface UserService {

  int getLimit(Authentication connectedUser, LimitType limitType);

  int getUsed(Authentication connectedUser, LimitType limitType);

  void incrementUsed(Authentication connectedUser, LimitType limitType);

}
