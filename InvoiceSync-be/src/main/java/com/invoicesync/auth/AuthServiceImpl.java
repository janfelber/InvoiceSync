package com.invoicesync.auth;

import static com.invoicesync.user.sidenav.SideNavUtils.buildMenuHierarchy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.invoicesync.user.Role;
import com.invoicesync.user.User;
import com.invoicesync.user.UserAccessDto;
import com.invoicesync.user.UserRepository;
import com.invoicesync.user.sidenav.SideNav;
import com.invoicesync.user.sidenav.SideNavRepository;
import com.invoicesync.user.sidenav.SidenavItemDto;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserDetailsService userDetailsService;

  private final JwtService jwtService;

  private final UserRepository userRepository;

  private final SideNavRepository sideNavRepository;

  private final PasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

  @Transactional
  @Override
  public void registerUser(final RegisterRequest registerRequest) {
    if (userRepository.existsByUsername(registerRequest.getUsername())) {
      throw new RuntimeException("Username already exists: " + registerRequest.getUsername());
    }

    final List<SideNav> defaultSidenav = sideNavRepository.findAllById(List.of(1L, 2L, 3L, 4L, 5L));

    final User user = User.builder()
        .fullName(registerRequest.getFullName())
        .username(registerRequest.getUsername())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .role(Role.ROLE_USER)
        .sidenav(new HashSet<>(defaultSidenav))
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

    final List<SidenavItemDto> sidenavItems = buildMenuHierarchy(user.getSidenav());

    final UserAccessDto userAccessDto = new UserAccessDto(sidenavItems);

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
    final List<SidenavItemDto> sidenavItems = buildMenuHierarchy(user.getSidenav());
    final UserAccessDto userAccess = new UserAccessDto(sidenavItems);

    return new LoginResponse(accessToken, refreshToken, userAccess);
  }

  @Override
  public void logout(final HttpServletRequest logoutRequest) {

  }

}
