package com.invoicesync.modules.auth.service;

import java.util.UUID;

import com.invoicesync.modules.auth.model.dto.RefreshSessionIssueResult;

public interface RefreshSessionService {

  RefreshSessionIssueResult issue(UUID userId, UUID familyId, String deviceInfo);

  RefreshSessionIssueResult rotate(String presentedRawToken, String deviceInfo);

  void revokeByRawToken(String rawToken);

  void revokeAllForUser(UUID userId);

}
