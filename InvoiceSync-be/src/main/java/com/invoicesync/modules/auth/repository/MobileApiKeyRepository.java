package com.invoicesync.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.auth.model.MobileApiKey;

public interface MobileApiKeyRepository extends JpaRepository<MobileApiKey, UUID> {

  Optional<MobileApiKey> findByApiKeyHashAndActiveTrue(String apiKeyHash);

}
