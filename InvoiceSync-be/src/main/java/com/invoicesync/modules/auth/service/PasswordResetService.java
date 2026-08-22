package com.invoicesync.modules.auth.service;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PasswordResetService {

  String createVerificationToken(String email);

  void sendPasswordForgotEmail(String email);

  void resetPassword(String token, String newPassword);

}
