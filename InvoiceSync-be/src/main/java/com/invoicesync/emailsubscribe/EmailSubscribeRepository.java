package com.invoicesync.emailsubscribe;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailSubscribeRepository extends JpaRepository<EmailSubscribe, Long> {
  boolean existsByEmail(String email);
}
