package com.invoicesync.core.audit;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(final String identifier) throws UsernameNotFoundException {
    User user;

    try {
      final UUID userId = UUID.fromString(identifier);
      user = userRepository.findById(userId)
          .orElseThrow(() -> new UsernameNotFoundException("User not found by ID: " + identifier));
    } catch (IllegalArgumentException ex) {
      // if it's not UUID its is probably login request
      user = userRepository.findByUsernameIgnoreCase(identifier.trim())
          .orElseThrow(() -> new UsernameNotFoundException("User not found by username: " + identifier));
    }

    final Collection<? extends GrantedAuthority> authorities = getAuthority(user);
    return new CustomUserDetails(user, authorities);
  }

  private Collection<? extends GrantedAuthority> getAuthority(final User user) {
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
    return List.of(authority);

  }

}
