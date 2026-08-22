package com.invoicesync.modules.auth.service;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EmailVerificationService {

  String createVerificationToken(UUID userId);

  void sendVerificationEmail(UUID userId, String email);

  void verifyEmail(String token);

}
