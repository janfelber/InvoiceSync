package com.invoicesync.modules.auth.service;

import jakarta.servlet.http.HttpServletRequest;

import com.invoicesync.modules.auth.model.LoginRequest;
import com.invoicesync.modules.auth.model.RegisterRequest;
import com.invoicesync.modules.auth.model.dto.AuthResult;

public interface AuthService {

  void registerUser(RegisterRequest registerRequest);

  AuthResult login(LoginRequest loginRequest, String deviceInfo);

  AuthResult refreshToken(HttpServletRequest refreshTokenRequest, String deviceInfo);

  void logout(HttpServletRequest logoutRequest);

  void logoutAllDevices(HttpServletRequest logoutRequest);

}
