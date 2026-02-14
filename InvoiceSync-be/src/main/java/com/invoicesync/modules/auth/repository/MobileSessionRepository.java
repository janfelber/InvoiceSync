package com.invoicesync.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.auth.model.MobileSession;

public interface MobileSessionRepository extends JpaRepository<MobileSession, UUID> {

  Optional<MobileSession> findBySessionToken(String sessionToken);

}
