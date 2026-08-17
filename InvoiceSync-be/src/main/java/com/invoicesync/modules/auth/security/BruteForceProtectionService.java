package com.invoicesync.modules.auth.security;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface BruteForceProtectionService {

  void loginSucceeded(String key);

  void loginFailed(String username, String ipAddress);

  boolean isBlocked(String key);

}
