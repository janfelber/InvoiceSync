package com.invoicesync.modules.subscription.guard.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.core.enums.LimitType;

public interface LimitGuardService {

  void checkLimit(Authentication connectedUser, LimitType type);

}
