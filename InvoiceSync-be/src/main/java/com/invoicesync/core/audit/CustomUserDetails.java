package com.invoicesync.core.audit;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.invoicesync.modules.user.model.User;

/**
 * Custom UserDetails implementation for Spring Security
 * to make sure that auditing is using UUID to get data for user
 * - getUsername() returns the user's UUID as a String (used for authentication and auditing).
 * - getRealUsername() returns the actual login username.
 * - getUser() gives access to the full User entity if needed.
 * - Authorities are based on the user's role.
 */
public class CustomUserDetails implements UserDetails {

  private final User user;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
    this.user = user;
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getId().toString();
  }

  public String getRealUsername() {
    return user.getUsername();
  }

  public User getUser() {
    return user;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
