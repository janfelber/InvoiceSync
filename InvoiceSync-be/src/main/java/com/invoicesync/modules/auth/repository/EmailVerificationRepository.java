package com.invoicesync.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.auth.model.EmailVerification;
import com.invoicesync.modules.user.model.User;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {

  Optional<EmailVerification> findByTokenHash(String tokenHash);

}
