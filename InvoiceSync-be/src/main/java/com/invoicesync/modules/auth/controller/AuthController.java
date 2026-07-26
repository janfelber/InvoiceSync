package com.invoicesync.modules.auth.controller;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.modules.auth.model.LoginRequest;
import com.invoicesync.modules.auth.model.LoginResponse;
import com.invoicesync.modules.auth.model.RegisterRequest;
import com.invoicesync.modules.auth.model.dto.AuthResult;
import com.invoicesync.modules.auth.security.RefreshCookieFactory;
import com.invoicesync.modules.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Api.AUTH)
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  private final RefreshCookieFactory refreshCookieFactory;

  @PostMapping(Api.REGISTER)
  public ResponseEntity<?> register(@Valid @RequestBody final RegisterRequest request) {
    authService.registerUser(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Api.LOGIN)
  public ResponseEntity<?> login(@Valid @RequestBody final LoginRequest request,
      final HttpServletRequest httpRequest, final HttpServletResponse response) {
    final AuthResult result = authService.login(request, httpRequest.getHeader("User-Agent"));
    attachRefreshCookie(response, result);
    return ResponseEntity.ok(new LoginResponse(result.getAccessToken(), result.getUser()));
  }

  @PostMapping(Api.REFRESH_TOKEN)
  public ResponseEntity<?> refreshToken(final HttpServletRequest request, final HttpServletResponse response) {
    final AuthResult result = authService.refreshToken(request, request.getHeader("User-Agent"));
    attachRefreshCookie(response, result);
    return ResponseEntity.ok(new LoginResponse(result.getAccessToken(), result.getUser()));
  }

  @PostMapping(Api.LOGOUT)
  public ResponseEntity<?> logout(final HttpServletRequest request, final HttpServletResponse response) {
    authService.logout(request);
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookieFactory.clear().toString());
    return ResponseEntity.ok().build();
  }

  @PostMapping(Api.LOGOUT_ALL)
  public ResponseEntity<?> logoutAllDevices(final HttpServletRequest request, final HttpServletResponse response) {
    authService.logoutAllDevices(request);
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookieFactory.clear().toString());
    return ResponseEntity.ok().build();
  }

  private void attachRefreshCookie(final HttpServletResponse response, final AuthResult result) {
    final Duration maxAge = Duration.between(LocalDateTime.now(), result.getRefreshExpiresAt());
    response.addHeader(HttpHeaders.SET_COOKIE,
        refreshCookieFactory.build(result.getRefreshToken(), maxAge).toString());
  }

}
