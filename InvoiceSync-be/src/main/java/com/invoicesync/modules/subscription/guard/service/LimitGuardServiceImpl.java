package com.invoicesync.modules.subscription.guard.service;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.core.enums.FeatureEnum;
import com.invoicesync.core.enums.LimitType;
import com.invoicesync.core.exception.LimitExceededException;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;
import com.invoicesync.modules.user.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LimitGuardServiceImpl implements LimitGuardService {

  private final UserService userLimitService;

  private final UserRepository userRepository;

  public void checkLimit(final Authentication connectedUser, final LimitType type) {
    final User user = userRepository.findById(UUID.fromString(connectedUser.getName()))
        .orElseThrow(() -> new IllegalStateException("User not found."));

    if (user.hasFeature(FeatureEnum.EKON_SPECIALTY)) {
      return;
    }

    final int used = userLimitService.getUsed(connectedUser, type);
    final int limit = userLimitService.getLimit(connectedUser, type);

    if (used >= limit) {
      throw new LimitExceededException("Limit pre " + type + " bol vyčerpaný.");
    }
  }

}
