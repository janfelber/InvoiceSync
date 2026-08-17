package com.invoicesync.modules.auth.security.protection.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * One row per lockout event. A lockout is considered active only while {@code banUntil} is in
 * the future ({@link BanLoginRepository#existsByUsernameAndBanUntilAfter}) — an expired row is
 * not deleted immediately, it's simply ignored by that check until the cleanup job removes it.
 */
@Getter
@Setter
@Entity
@Table(name = "ban_login", schema = "invoice_sync")
public class BanLogin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  @Column(name = "ban_until")
  private LocalDateTime banUntil;

}
