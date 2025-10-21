package com.invoicesync.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody final RegisterRequest request) {
    authService.registerUser(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody final LoginRequest request, final HttpServletResponse response) {
    final LoginResponse loginResponse = authService.login(request);

    ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
        .httpOnly(true)      // aby JS nemohol čítať token
        .secure(false)        // HTTPS required
        .path("/")           // dostupné pre celý frontend a API
        .maxAge(7 * 24 * 60 * 60)
        .sameSite("Lax")     // alebo "Strict" podľa potreby
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.ok(loginResponse);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(final HttpServletRequest request) {
    final LoginResponse loginResponse = authService.refreshToken(request);
    return ResponseEntity.ok(loginResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(final HttpServletResponse response) {
    final ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(0)
        .sameSite("None")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.ok().build();
  }
}
