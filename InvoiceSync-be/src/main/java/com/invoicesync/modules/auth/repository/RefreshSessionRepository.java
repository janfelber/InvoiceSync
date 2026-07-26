package com.invoicesync.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.invoicesync.modules.auth.model.RefreshSession;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {

  Optional<RefreshSession> findByTokenHash(String tokenHash);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE RefreshSession session SET session.revoked = true, session.revokedAt = CURRENT_TIMESTAMP, "
      + "session.lastUsedAt = CURRENT_TIMESTAMP "
      + "WHERE session.tokenHash = :tokenHash AND session.revoked = false AND session.expiresAt > CURRENT_TIMESTAMP")
  int consumeByTokenHash(@Param("tokenHash") String tokenHash);

  @Modifying
  @Query("UPDATE RefreshSession session SET session.revoked = true, session.revokedAt = CURRENT_TIMESTAMP "
      + "WHERE session.familyId = :familyId AND session.revoked = false ")
  int revokeFamilyByFamilyId(@Param("familyId") UUID familyId);

  @Modifying
  @Query("UPDATE RefreshSession session SET session.revoked = true, session.revokedAt = CURRENT_TIMESTAMP "
      + "WHERE session.userId = :userId AND session.revoked = false ")
  int revokeAllByUserId(@Param("userId") UUID userId);

}
