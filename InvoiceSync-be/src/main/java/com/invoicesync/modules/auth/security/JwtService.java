package com.invoicesync.modules.auth.security;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.invoicesync.core.exception.InvalidTokenException;

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

  @Value("${security.jwt.issuer}")
  private String jwtIssuer;

  @Value("${security.jwt.audience}")
  private String jwtAudience;

  public String generateToken(final Authentication authentication) {
    final UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
    final Date now = new Date();
    final Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
        .header().add("typ", "JWT").and()
        .subject(userPrincipal.getUsername())
        .id(UUID.randomUUID().toString())
        .issuer(jwtIssuer)
        .audience().add(jwtAudience).and()
        .claim("tokenType", "access")
        .issuedAt(new Date())
        .expiration(expiryDate)
        .signWith(getSignInKey())
        .compact();
  }

  private SecretKey getSignInKey() {
    final byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String getUsernameFromToken(final String token) {
    return parseAccessToken(token).getSubject();
  }

  public Claims parseAccessToken(final String token) {
    final Claims claims = extractAllClaims(token);
    if (!"access".equals(claims.get("tokenType", String.class))) {
      throw new InvalidTokenException("Not an access token");
    }

    return claims;
  }

  private Claims extractAllClaims(final String token) {
    try {
      return Jwts.parser()
          .verifyWith(getSignInKey())
          .requireIssuer(jwtIssuer)
          .requireAudience(jwtAudience)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      throw new InvalidTokenException("Token invalid or expired", e);
    }

  }

}
