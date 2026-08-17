package com.invoicesync.modules.auth.security.protection.model;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

  long countByUsernameAndAttemptDateAfter(String username, LocalDateTime since);

}
