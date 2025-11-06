package com.invoicesync.modules.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.user.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Optional<User> findById(UUID id);

}
