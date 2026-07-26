package com.invoicesync.modules.auth.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicesync.core.exception.InvalidRefreshTokenException;
import com.invoicesync.modules.auth.model.RefreshSession;
import com.invoicesync.modules.auth.model.dto.RefreshSessionIssueResult;
import com.invoicesync.modules.auth.repository.RefreshSessionRepository;
import com.invoicesync.modules.auth.security.RefreshTokenUtil;

@Service
@Transactional
public class RefreshSessionServiceImpl implements RefreshSessionService {

  private final RefreshSessionRepository refreshSessionRepository;

  @Value("${security.jwt.refresh-token.expiration}")
  private long refreshExpirationMs;

  public RefreshSessionServiceImpl(final RefreshSessionRepository refreshSessionRepository) {
    this.refreshSessionRepository = refreshSessionRepository;
  }

  @Override
  public RefreshSessionIssueResult issue(final UUID userId, final UUID familyId, final String deviceInfo) {
    final String rawToken = RefreshTokenUtil.generate();
    final String hashToken = RefreshTokenUtil.hash(rawToken);

    final RefreshSession session = new RefreshSession();

    session.setTokenHash(hashToken);
    session.setUserId(userId);
    session.setFamilyId(familyId != null ? familyId : UUID.randomUUID());
    session.setDeviceInfo(deviceInfo);
    session.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshExpirationMs)));

    refreshSessionRepository.save(session);

    return new RefreshSessionIssueResult(rawToken, session.getExpiresAt(), userId);
  }

  @Override
  public RefreshSessionIssueResult rotate(final String presentedRawToken, final String deviceInfo) {
    final String presentedTokenHash = RefreshTokenUtil.hash(presentedRawToken);
    final RefreshSession refreshSession = refreshSessionRepository.findByTokenHash(presentedTokenHash).orElseThrow(() ->
        new InvalidRefreshTokenException("Refresh token not recognized"));

    final int consumed = refreshSessionRepository.consumeByTokenHash(presentedTokenHash);
    if (consumed == 1) {
      return issue(refreshSession.getUserId(), refreshSession.getFamilyId(), deviceInfo);
    }

    final RefreshSession currentState = refreshSessionRepository.findByTokenHash(presentedTokenHash)
        .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not recognized"));

    if (currentState.isRevoked()) {
      refreshSessionRepository.revokeFamilyByFamilyId(currentState.getFamilyId());
      throw new InvalidRefreshTokenException("Refresh token reuse detected, session family revoked");
    }

    throw new InvalidRefreshTokenException("Refresh token expired");
  }

  @Override
  public void revokeByRawToken(final String rawToken) {
    final String hashToken = RefreshTokenUtil.hash(rawToken);
    refreshSessionRepository.consumeByTokenHash(hashToken);
  }

  @Override
  public void revokeAllForUser(final UUID userId) {
    refreshSessionRepository.revokeAllByUserId(userId);
  }

}
