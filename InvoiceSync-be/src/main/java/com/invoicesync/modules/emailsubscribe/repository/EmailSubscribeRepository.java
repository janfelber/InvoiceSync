package com.invoicesync.modules.emailsubscribe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.emailsubscribe.model.EmailSubscribe;

public interface EmailSubscribeRepository extends JpaRepository<EmailSubscribe, Long> {
  boolean existsByEmail(String email);
}
