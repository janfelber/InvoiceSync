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
@Table(name = "mobile_api_key", schema = "invoice_sync")
public class MobileApiKey {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column(name = "api_key_hash", nullable = false, unique = true)
  private String apiKeyHash;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "device_name")
  private String deviceName;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "last_used_at")
  private LocalDateTime lastUsedAt;

  @Column(nullable = false)
  private boolean active;

  @PrePersist
  void prePersist() {
    createdAt = LocalDateTime.now();
    active = true;
  }

}
