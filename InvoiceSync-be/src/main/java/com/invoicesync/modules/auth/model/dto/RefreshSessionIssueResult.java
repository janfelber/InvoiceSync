package com.invoicesync.modules.auth.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshSessionIssueResult {

  private String rawToken;

  private LocalDateTime expiresAt;

  private UUID userId;

}
