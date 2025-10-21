package com.invoicesync.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

  @Value("${security.jwt.secret-key}")
  private String jwtSecret;

  @Value("${security.jwt.expiration}")
  private long jwtExpirationMs;

  @Value("${security.jwt.refresh-token.expiration}")
  private long refreshExpirationMs;

  // generate the token
  public String generateToken(final Authentication authentication) {
    return generateAccessToken(authentication, jwtExpirationMs, new HashMap<>());
  }

  private String generateAccessToken(final Authentication authentication, final long expirationMs,
      final Map<String, String> claims) {
    final UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

    final Date now = new Date();
    final Date expiryDate = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
        .header()
        .add("typ", "JWT")
        .and()
        .subject(userPrincipal.getUsername())
        .claims(claims)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(getSignInKey())
        .compact();
  }

  private SecretKey getSignInKey() {
    final byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // generate the referes
  public String generateRefreshToken(final Authentication authentication) {
    final Map<String, String> claims = new HashMap<>();
    claims.put("tokenType", "refresh");

    return generateAccessToken(authentication, refreshExpirationMs, claims);
  }

  // validate roken
  public boolean validateTokenForUser(final String authToken, final UserDetails userDetails) {
    final String username = getUsernameFromToken(authToken);

    return username != null && username.equals(userDetails.getUsername());
  }

  public boolean isValidToken(final String token) {
    return extractAllClaims(token) != null;
  }

  public String getUsernameFromToken(final String token) {
    final Claims claims = extractAllClaims(token);
    if (claims != null) {
      return claims.getSubject();
    }

    return null;
  }

  // valideate if token is refesrsh
  public boolean isRefreshToken(final String authToken) {
    final Claims claims = extractAllClaims(authToken);

    if (claims == null) {
      return false;
    }

    return "refresh".equals(claims.get("tokenType"));
  }

  public TokenPair generateTokenPair(final Authentication authentication) {
    final String accessToken = generateToken(authentication);
    final String refreshToken = generateRefreshToken(authentication);

    return new TokenPair(accessToken, refreshToken);

  }

  private Claims extractAllClaims(final String authToken) {
    Claims claims = null;
    try {
      claims = Jwts.parser()
          .verifyWith(getSignInKey())
          .build()
          .parseSignedClaims(authToken)
          .getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    }

    return claims;
  }

}
