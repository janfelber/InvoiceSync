package com.invoicesync.modules.auth.security;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshCookieFactory {

  private final RefreshCookieProperties properties;

  public RefreshCookieFactory(final RefreshCookieProperties properties) {
    this.properties = properties;
  }

  public ResponseCookie build(final String rawToken, final Duration maxAge) {
    return ResponseCookie.from(properties.getName(), rawToken)
        .httpOnly(true)
        .secure(properties.isSecure())
        .path(properties.getPath())
        .sameSite(properties.getSameSite())
        .maxAge(maxAge)
        .build();
  }

  public ResponseCookie clear() {
    return ResponseCookie.from(properties.getName(), "")
        .httpOnly(true)
        .secure(properties.isSecure())
        .path(properties.getPath())
        .sameSite(properties.getSameSite())
        .maxAge(0)
        .build();
  }

}
