package com.invoicesync.modules.auth.security;

import java.time.LocalDateTime;

import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicesync.modules.auth.security.protection.model.BanLogin;
import com.invoicesync.modules.auth.security.protection.model.BanLoginRepository;
import com.invoicesync.modules.auth.security.protection.model.LoginAttempt;
import com.invoicesync.modules.auth.security.protection.model.LoginAttemptRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@NullMarked
@RequiredArgsConstructor
@Slf4j
public class BruteForceProtectionServiceImpl implements BruteForceProtectionService {

  private static final int MAX_ATTEMPT = 5;

  private static final long LOCK_TIME_MINUTES = 15;

  private final LoginAttemptRepository loginAttemptRepository;

  private final BanLoginRepository banLoginRepository;

  @Override
  public void loginSucceeded(final String key) {

  }

  @Override
  public void loginFailed(final String username, final String ipAddress) {
    LoginAttempt loginAttempt = new LoginAttempt();
    loginAttempt.setUsername(username);
    loginAttempt.setIpAddress(ipAddress);
    loginAttemptRepository.save(loginAttempt);

    long recentAttempts = loginAttemptRepository.countByUsernameAndAttemptDateAfter(
        username, LocalDateTime.now().minusMinutes(LOCK_TIME_MINUTES));

    if (!isBlocked(username) && recentAttempts >= MAX_ATTEMPT) {
      log.warn("[AUTH] Account locked after {} failed attempts: username={}, ipAddress={}", recentAttempts,
          username, ipAddress);
      BanLogin banLogin = new BanLogin();
      banLogin.setUsername(username);
      banLogin.setBanUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES));
      banLoginRepository.save(banLogin);
    }
  }

  @Override
  public boolean isBlocked(final String username) {
    return banLoginRepository.existsByUsernameAndBanUntilAfter(username, LocalDateTime.now());
  }

}
