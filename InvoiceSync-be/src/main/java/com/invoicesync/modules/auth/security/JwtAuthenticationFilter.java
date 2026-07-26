package com.invoicesync.modules.auth.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.invoicesync.core.audit.CustomUserDetails;
import com.invoicesync.core.exception.InvalidTokenException;
import com.invoicesync.modules.auth.service.MobileAuthService;
import com.invoicesync.modules.user.model.User;

import io.jsonwebtoken.Claims;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  private final UserDetailsService userDetailsService;

  private final MobileAuthService mobileAuthService;

  public JwtAuthenticationFilter(final JwtService jwtService, final UserDetailsService userDetailsService,
      final MobileAuthService mobileAuthService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
    this.mobileAuthService = mobileAuthService;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    final String authorizationHeader = request.getHeader("Authorization");

    final String jwt;
    final String username;

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = getJwtFromRequest(request);

    // check if the token is valid and if it's an API key for mobile or a JWT token
    if (ApiKeyUtil.isApiKey(jwt)) {
      User user = mobileAuthService.validateApiKey(jwt);
      if (user != null) {
        List<SimpleGrantedAuthority> authorities = user.getRole() != null
            ? List.of(new SimpleGrantedAuthority(user.getRole().name()))
            : List.of();
        CustomUserDetails userDetails = new CustomUserDetails(user, authorities);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
      filterChain.doFilter(request, response);
    } else {
      try {
        Claims claims = jwtService.parseAccessToken(jwt);
        String userId = claims.getSubject();

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          final UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);
          final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );
          authToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request)
          );
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }

      } catch (InvalidTokenException | UsernameNotFoundException e) {
        SecurityContextHolder.clearContext();
      }
      filterChain.doFilter(request, response);
    }
  }

  private String getJwtFromRequest(final HttpServletRequest request) {
    final String authorizationHeader = request.getHeader("Authorization");
    return authorizationHeader.substring(7);
  }

}
