package com.invoicesync.modules.subscription.guard.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.core.exception.LimitExceededException;
import com.invoicesync.modules.user.service.UserService;
import com.invoicesync.core.enums.LimitType;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LimitGuardServiceImpl implements LimitGuardService{

  private final UserService userLimitService;

  public void checkLimit(final Authentication connectedUser, final LimitType type) {
    final int used = userLimitService.getUsed(connectedUser, type);
    final int limit = userLimitService.getLimit(connectedUser, type);

    if (used >= limit) {
      throw new LimitExceededException("Limit pre " + type + " bol vyčerpaný.");
    }
  }

}
