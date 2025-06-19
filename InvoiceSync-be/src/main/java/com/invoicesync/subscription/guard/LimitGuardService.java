package com.invoicesync.subscription.guard;

import org.springframework.security.core.Authentication;

import com.invoicesync.subscription.enums.LimitType;

public interface LimitGuardService {

  void checkLimit(Authentication connectedUser, LimitType type);

}
