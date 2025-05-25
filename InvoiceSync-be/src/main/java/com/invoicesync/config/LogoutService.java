package com.invoicesync.config;

import lombok.RequiredArgsConstructor;

// @Service
@RequiredArgsConstructor
public class LogoutService { //implements LogoutHandler {

  // private final TokenRepository tokenRepository;
  //
  // @Override
  // public void logout(final HttpServletRequest request, final HttpServletResponse response,
  //     final Authentication authentication) {
  //
  //   final String authHeader = request.getHeader("Authorization");
  //   final String jwtToken;
  //
  //   if (authHeader == null || !authHeader.startsWith("Bearer ")) {
  //     return;
  //   }
  //
  //   jwtToken = authHeader.substring(7);
  //   var storedToken = tokenRepository.findByToken(jwtToken).orElse(null);
  //
  //   if (storedToken != null) {
  //     storedToken.setExpired(true);
  //     storedToken.setRevoked(true);
  //     tokenRepository.save(storedToken);
  //   }
  //
  // }

}
