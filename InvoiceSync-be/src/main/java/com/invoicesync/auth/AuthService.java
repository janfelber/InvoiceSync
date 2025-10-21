package com.invoicesync.auth;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

  void registerUser(RegisterRequest registerRequest);

  LoginResponse login(LoginRequest loginRequest);

  LoginResponse refreshToken(HttpServletRequest refreshTokenRequest);

  void logout(HttpServletRequest logoutRequest);

}
