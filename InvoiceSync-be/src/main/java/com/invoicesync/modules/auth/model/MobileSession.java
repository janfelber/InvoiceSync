package com.invoicesync.modules.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mobile_session", schema = "invoice_sync")
public class MobileSession {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column(name = "session_token", nullable = false, unique = true)
  private String sessionToken;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(nullable = false)
  private boolean used;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  void prePersist() {
    createdAt = LocalDateTime.now();
  }

}
