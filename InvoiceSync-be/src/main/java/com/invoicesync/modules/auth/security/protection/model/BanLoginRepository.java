package com.invoicesync.modules.auth.security.protection.model;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BanLoginRepository extends JpaRepository<BanLogin, Long> {

  boolean existsByUsername(String username);
  
  boolean existsByUsernameAndBanUntilAfter(String username, LocalDateTime banUntil);

}
