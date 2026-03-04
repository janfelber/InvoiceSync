package com.invoicesync.modules.auth.controller;

import java.time.Duration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.modules.auth.model.LoginRequest;
import com.invoicesync.modules.auth.model.LoginResponse;
import com.invoicesync.modules.auth.model.RegisterRequest;
import com.invoicesync.modules.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Api.AUTH)
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping(Api.REGISTER)
  public ResponseEntity<?> register(@Valid @RequestBody final RegisterRequest request) {
    authService.registerUser(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Api.LOGIN)
  public ResponseEntity<?> login(@Valid @RequestBody final LoginRequest request, final HttpServletResponse response) {
    final LoginResponse loginResponse = authService.login(request);

    // 🔒 refreshToken ide iba do HttpOnly cookie
    ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
        .httpOnly(true)
        .secure(false) // nastav na false ak nemáš HTTPS počas vývoja
        .path("/auth/refresh-token")
        .maxAge(Duration.ofDays(7))
        .sameSite("Strict")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    // 🔐 klient dostane len accessToken + user info
    return ResponseEntity.ok(new LoginResponse(
        loginResponse.getAccessToken(),
        null, // refresh token odstránime z JSON
        loginResponse.getUser()
    ));
  }

  @PostMapping(Api.REFRESH_TOKEN)
  public ResponseEntity<?> refreshToken(final HttpServletRequest request) {
    final LoginResponse loginResponse = authService.refreshToken(request);
    return ResponseEntity.ok(loginResponse);
  }

  @PostMapping(Api.LOGOUT)
  public ResponseEntity<?> logout(final HttpServletRequest request, final HttpServletResponse response) {
    authService.logout(request);
    final ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(false)
        .path("/auth/refresh-token")
        .maxAge(0)
        .sameSite("None")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.ok().build();
  }

}
