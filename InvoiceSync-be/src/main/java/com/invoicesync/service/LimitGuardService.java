package com.invoicesync.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.subscription.LimitType;

public interface LimitGuardService {

  void checkLimit(Authentication connectedUser, LimitType type);

}
