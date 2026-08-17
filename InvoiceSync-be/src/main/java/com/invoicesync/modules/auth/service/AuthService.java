package com.invoicesync.modules.auth.service;

import jakarta.servlet.http.HttpServletRequest;

import org.jspecify.annotations.NullMarked;

import com.invoicesync.modules.auth.model.LoginRequest;
import com.invoicesync.modules.auth.model.RegisterRequest;
import com.invoicesync.modules.auth.model.dto.AuthResult;

@NullMarked
public interface AuthService {

  void registerUser(RegisterRequest registerRequest);

  //TODO take HttpServletRequest instead and extract deviceInfo/ipAddress inside, like refreshToken/logout do
  AuthResult login(LoginRequest loginRequest, String deviceInfo, String ipAddress);

  AuthResult refreshToken(HttpServletRequest refreshTokenRequest, String deviceInfo);

  void logout(HttpServletRequest logoutRequest);

  void logoutAllDevices(HttpServletRequest logoutRequest);

}
