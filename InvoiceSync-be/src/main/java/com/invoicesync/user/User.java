package com.invoicesync.user;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "app_user", schema = "invoice_sync")
public class User {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "full_name")
  private String fullName;

  private String username;

  private String password;

  private String email;

  @Column(name = "phone")
  private String phoneNumber;

  @CreationTimestamp
  @Column(name = "created_on", updatable = false)
  private Date createdOn;

  @Enumerated(EnumType.STRING)
  private Role role;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "user_features",
      schema = "invoice_sync",
      joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "code")
  private Set<Long> featureIds = new HashSet<>();

  public User(final String fullName, final String username, final String password, final Role role) {
    this.fullName = fullName;
    this.username = username;
    this.password = password;
    this.role = role;
  }

}
