package com.invoicesync.modules.auth.service;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.invoicesync.core.audit.CustomUserDetails;
import com.invoicesync.core.enums.FeatureEnum;
import com.invoicesync.core.enums.Role;
import com.invoicesync.core.exception.CompanyRegistrationNumberExists;
import com.invoicesync.core.exception.CompanyRegistrationNumberNotFound;
import com.invoicesync.core.exception.InvalidRefreshTokenException;
import com.invoicesync.core.exception.InvalidTokenException;
import com.invoicesync.core.exception.RegisterEmailExists;
import com.invoicesync.core.exception.UserNameExists;
import com.invoicesync.modules.activity.model.UserActivityType;
import com.invoicesync.modules.activity.service.UserActivityRecord;
import com.invoicesync.modules.activity.service.UserActivityService;
import com.invoicesync.modules.auth.model.LoginRequest;
import com.invoicesync.modules.auth.model.RegisterRequest;
import com.invoicesync.modules.auth.model.dto.AuthResult;
import com.invoicesync.modules.auth.model.dto.RefreshSessionIssueResult;
import com.invoicesync.modules.auth.security.JwtService;
import com.invoicesync.modules.auth.security.RefreshCookieProperties;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.model.UserAccessDto;
import com.invoicesync.modules.user.repository.UserRepository;
import com.invoicesync.supplier.CompaniesRegistry;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserDetailsService userDetailsService;

  private final UserActivityService userActivityService;

  private final RefreshSessionService refreshSessionService;

  private final JwtService jwtService;

  private final UserRepository userRepository;

  private final CompaniesRegistry companiesRegistry;

  private final PasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

  private final RefreshCookieProperties refreshCookieProperties;

  @Transactional
  @Override
  public void registerUser(final RegisterRequest registerRequest) {
    if (userRepository.existsByUsername(registerRequest.getUsername())) {
      throw new UserNameExists("Username already exists: " + registerRequest.getUsername());
    }

    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      throw new RegisterEmailExists("Email already exists: " + registerRequest.getEmail());
    }

    if (userRepository.existsByRegistrationNumber(registerRequest.getRegistrationNumber())) {
      throw new CompanyRegistrationNumberExists(
          "Registration number already exists: " + registerRequest.getRegistrationNumber());
    }

    if (!companiesRegistry.findByRegistrationNumber(registerRequest.getRegistrationNumber()).isPresent()) {
      throw new CompanyRegistrationNumberNotFound(
          "Registration number not found: " + registerRequest.getRegistrationNumber());
    }

    final User user = User.builder()
        .email(registerRequest.getEmail())
        .username(registerRequest.getUsername())
        .fullName(registerRequest.getFullName())
        .registrationNumber(registerRequest.getRegistrationNumber())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .phoneNumber(registerRequest.getPhoneNumber())
        .role(Role.ROLE_USER)
        .build();

    userRepository.save(user);
  }

  @Override
  public AuthResult login(final LoginRequest loginRequest, final String deviceInfo) {
    final Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    final User user = userRepository.findByUsername(loginRequest.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    final String accessToken = jwtService.generateToken(authentication);

    final RefreshSessionIssueResult session = refreshSessionService.issue(user.getId(), null, deviceInfo);

    final Set<String> featureNames = user.getFeatureIds().stream()
        .map(FeatureEnum::fromId)
        .map(Enum::name)
        .collect(Collectors.toSet());

    final UserAccessDto userAccessDto = new UserAccessDto(user.getRole().name(), featureNames);

    userActivityService.save(UserActivityRecord.forType(String.valueOf(user.getId()), UserActivityType.LOGGED_IN,
        "User logged in"));

    return new AuthResult(accessToken, session.getRawToken(), session.getExpiresAt(), userAccessDto);
  }

  @Override
  public AuthResult refreshToken(final HttpServletRequest request, final String deviceInfo) {
    final String rawRefreshToken = Optional.ofNullable(request.getCookies())
        .flatMap(cookies -> Arrays.stream(cookies)
            .filter(cookie -> refreshCookieProperties.getName().equals(cookie.getName()))
            .findFirst())
        .map(Cookie::getValue)
        .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

    final RefreshSessionIssueResult rotated = refreshSessionService.rotate(rawRefreshToken, deviceInfo);

    final User user =
        userRepository.findById(rotated.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

    final UserDetails userDetails = userDetailsService.loadUserByUsername(rotated.getUserId().toString());

    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );

    final String accessToken = jwtService.generateToken(authentication);

    final Set<String> featureNames = user.getFeatureIds().stream()
        .map(FeatureEnum::fromId)
        .map(Enum::name)
        .collect(Collectors.toSet());

    final UserAccessDto userAccess = new UserAccessDto(user.getRole().name(), featureNames);

    return new AuthResult(accessToken, rotated.getRawToken(), rotated.getExpiresAt(), userAccess);
  }

  @Override
  public void logout(final HttpServletRequest request) {
    try {
      final String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        log.warn("Logout called without Bearer token - activity will not be logged");
      } else {
        final String userId = jwtService.getUsernameFromToken(authHeader.substring(7));
        if (userId != null) {
          userActivityService.save(UserActivityRecord.forType(
              userId,
              UserActivityType.LOGGED_OUT,
              "User logged out"
          ));
        }
      }
    } catch (InvalidTokenException e) {
      log.debug("Logout called with an already-invalid access token - activity will not be logged");
    }

    Optional.ofNullable(request.getCookies())
        .flatMap(cookies -> Arrays.stream(cookies)
            .filter(cookie -> refreshCookieProperties.getName().equals(cookie.getName()))
            .findFirst())
        .map(Cookie::getValue)
        .ifPresent(refreshSessionService::revokeByRawToken);
  }

  @Override
  public void logoutAllDevices(final HttpServletRequest request) {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof final CustomUserDetails userDetails)) {
      throw new InvalidTokenException("Authentication required");
    }

    refreshSessionService.revokeAllForUser(userDetails.getUser().getId());
  }

}
