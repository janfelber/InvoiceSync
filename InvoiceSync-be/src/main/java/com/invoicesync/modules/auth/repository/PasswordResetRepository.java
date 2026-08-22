package com.invoicesync.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.auth.model.PasswordReset;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {

  Optional<PasswordReset> findByTokenHash(String tokenHash);

}
