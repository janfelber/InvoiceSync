package com.invoicesync.modules.activity.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_log_user_activity", schema = "invoice_sync")
public class UserActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String user;

  @Enumerated(EnumType.STRING)
  private UserActivityType type;

  private String description;

  private LocalDateTime timestamp;

}
