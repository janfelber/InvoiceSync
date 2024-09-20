package com.invoicesync.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.invoicesync.token.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;

  @Override
  public void logout(final HttpServletRequest request, final HttpServletResponse response,
      final Authentication authentication) {

    final String authHeader = request.getHeader("Authorization");
    final String jwtToken;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }

    jwtToken = authHeader.substring(7);
    var storedToken = tokenRepository.findByToken(jwtToken).orElse(null);

    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
    }

  }

}
