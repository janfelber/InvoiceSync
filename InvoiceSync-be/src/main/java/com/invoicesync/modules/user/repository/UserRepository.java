package com.invoicesync.modules.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.core.enums.Role;
import com.invoicesync.modules.user.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsernameIgnoreCase(String username);

  Boolean existsByUsernameIgnoreCase(String username);

  Optional<User> findById(UUID id);

  boolean existsByRegistrationNumber(String registrationNumber);

  boolean existsByEmailIgnoreCase(String email);

  Page<User> findByRoleNot(Role role, Pageable pageable);

  Optional<User> findByEmailIgnoreCase(String email);

}
