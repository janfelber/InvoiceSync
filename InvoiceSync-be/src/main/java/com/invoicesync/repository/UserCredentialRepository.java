package com.invoicesync.repository;

import com.invoicesync.module.UserCredential;
import com.invoicesync.user.UserDemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserDemo, Long> {

    Optional<UserDemo> findByLogin(String login);

}
