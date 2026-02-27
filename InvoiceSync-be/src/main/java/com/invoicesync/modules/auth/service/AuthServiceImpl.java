package com.invoicesync.modules.auth.service;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
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

import com.invoicesync.core.enums.FeatureEnum;
import com.invoicesync.core.enums.Role;
import com.invoicesync.core.exception.CompanyRegistrationNumberExists;
import com.invoicesync.core.exception.CompanyRegistrationNumberNotFound;
import com.invoicesync.core.exception.RegisterEmailExists;
import com.invoicesync.core.exception.UserNameExists;
import com.invoicesync.modules.auth.model.LoginRequest;
import com.invoicesync.modules.auth.model.LoginResponse;
import com.invoicesync.modules.auth.model.RegisterRequest;
import com.invoicesync.modules.auth.model.TokenPair;
import com.invoicesync.modules.auth.security.JwtService;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.model.UserAccessDto;
import com.invoicesync.modules.user.repository.UserRepository;
import com.invoicesync.partner.CompaniesRegistry;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserDetailsService userDetailsService;

  private final JwtService jwtService;

  private final UserRepository userRepository;

  private final CompaniesRegistry companiesRegistry;

  private final PasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

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

    if (!companiesRegistry.findByIco(registerRequest.getRegistrationNumber()).isPresent()) {
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
  public LoginResponse login(final LoginRequest loginRequest) {
    final Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    final TokenPair tokenPair = jwtService.generateTokenPair(authentication);

    final User user = userRepository.findByUsername(loginRequest.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    final Set<String> featureNames = user.getFeatureIds().stream()
        .map(FeatureEnum::fromId)
        .map(Enum::name)
        .collect(Collectors.toSet());

    final UserAccessDto userAccessDto = new UserAccessDto(user.getRole().name(), featureNames);

    return new LoginResponse(
        tokenPair.getAccessToken(),
        tokenPair.getRefreshToken(),
        userAccessDto);
  }

  @Override
  public LoginResponse refreshToken(final HttpServletRequest request) {
    final Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      throw new RuntimeException("No cookies found");
    }

    final String refreshToken = Arrays.stream(cookies)
        .filter(c -> "refreshToken".equals(c.getName()))
        .findFirst()
        .map(Cookie::getValue)
        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

    if (!jwtService.isRefreshToken(refreshToken)) {
      throw new RuntimeException("Invalid refresh token");
    }

    final String userIdStr = jwtService.getUsernameFromToken(refreshToken);
    final UUID userId = UUID.fromString(userIdStr);

    final User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    final UserDetails userDetails = userDetailsService.loadUserByUsername(userIdStr);

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

    return new LoginResponse(accessToken, refreshToken, userAccess);
  }

  @Override
  public void logout(final HttpServletRequest logoutRequest) {

  }

}
