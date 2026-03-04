package com.invoicesync.modules.auth.service;

import jakarta.servlet.http.HttpServletRequest;

import com.invoicesync.modules.auth.model.LoginRequest;
import com.invoicesync.modules.auth.model.LoginResponse;
import com.invoicesync.modules.auth.model.RegisterRequest;

public interface AuthService {

  void registerUser(RegisterRequest registerRequest);

  LoginResponse login(LoginRequest loginRequest);

  LoginResponse refreshToken(HttpServletRequest refreshTokenRequest);

  void logout(HttpServletRequest logoutRequest);

}
