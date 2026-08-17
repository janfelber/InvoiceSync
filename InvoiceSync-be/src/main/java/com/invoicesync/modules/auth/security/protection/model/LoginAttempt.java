package com.invoicesync.modules.auth.security.protection.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * One row per failed login attempt. Append-only audit log — never deleted or reset on a
 * successful login; rows only stop counting once they age out of the lockout window, and are
 * only physically removed by the cleanup job.
 */
@Getter
@Setter
@Entity
@Table(name = "login_attempt", schema = "invoice_sync")
public class LoginAttempt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  @Column(name = "ip")
  private String ipAddress;

  @CreationTimestamp
  @Column(name = "attempted_at", updatable = false)
  private LocalDateTime attemptDate;

}
