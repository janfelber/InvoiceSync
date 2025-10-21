package com.invoicesync.user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.invoicesync.user.sidenav.SideNav;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

  @Enumerated(EnumType.STRING)
  private Role role;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_sidenav",
      schema = "invoice_sync",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "sidenav_id")
  )
  private Set<SideNav> sidenav = new HashSet<>();

  public User(final String fullName, final String username, final String password, final Role role, final Set<SideNav> sidenav) {
    this.fullName = fullName;
    this.username = username;
    this.password = password;
    this.role = role;
    this.sidenav = sidenav;
  }

}
