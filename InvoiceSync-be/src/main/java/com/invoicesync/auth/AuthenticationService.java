package com.invoicesync.auth;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationService {

    // private final UserCredentialRepository repository;
    //
    // private final TokenRepository tokenRepository;
    //
    // private final PasswordEncoder passwordEncoder;
    //
    // private final JwtService jwtService;
    //
    // private final AuthenticationManager authenticationManager;
    //
    // private final TwoFactorAuthenticationService tfaService;
    //
    // private final UserCredentialService userCredentialService;
    //
    // public AuthenticationResponse register(final RegisterRequest request) {
    //     final var user = UserDemo.builder()
    //             .username(request.getUsername())
    //             .login(request.getLogin())
    //             .password(passwordEncoder.encode(request.getPassword()))
    //             .role(Role.USER)
    //             .mfa_enabled(request.isMfaEnabled())
    //             .build();
    //
    //     // if MFA is enabled, generate a secret key and save it to the user
    //     if (request.isMfaEnabled()) {
    //         user.setSecret(tfaService.generateNewSecret());
    //     }
    //     final var savedUser = repository.save(user);
    //
    //     final Long userId = savedUser.getId();
    //     userCredentialService.createUserDirectory(userId);
    //
    //     final var jwtToken = jwtService.generateToken(user);
    //     final var refreshToken = jwtService.generateRefreshToken(user);
    //     saveUserToken(savedUser, jwtToken);
    //     return AuthenticationResponse.builder()
    //             .secretImageUri(tfaService.generateQrCodeImageUri(user.getSecret()))
    //             .accessToken(jwtToken)
    //             .refreshToken(refreshToken)
    //             .mfaEnabled(user.isMfa_enabled())
    //             .build();
    // }
    //
    // public AuthenticationResponse authenticate(final AuthenticationRequest request) {
    //     authenticationManager.authenticate(
    //             new UsernamePasswordAuthenticationToken(
    //                     request.getLogin(),
    //                     request.getPassword())
    //     );
    //     final var user = repository.findByLogin(request.getLogin())
    //             .orElseThrow();
    //     if (user.isMfa_enabled()) {
    //         return AuthenticationResponse.builder()
    //                 .accessToken("")
    //                 .refreshToken("")
    //                 .mfaEnabled(true)
    //                 .build();
    //     }
    //     final var jwtToken = jwtService.generateToken(user);
    //     final var refreshToken = jwtService.generateRefreshToken(user);
    //
    //     revokeAllUsersTokens(user);
    //     saveUserToken(user, jwtToken);
    //
    //     return AuthenticationResponse.builder()
    //             .accessToken(jwtToken)
    //             .refreshToken(refreshToken)
    //             .mfaEnabled(false)
    //             .build();
    // }
    //
    //
    // private void revokeAllUsersTokens(final UserDemo user) {
    //     final var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
    //     if (validUserTokens.isEmpty()) {
    //         return;
    //     }
    //     validUserTokens.forEach(token -> {
    //         token.setExpired(true);
    //         token.setRevoked(true);
    //     });
    //     tokenRepository.saveAll(validUserTokens);
    // }
    //
    // private void saveUserToken(final UserDemo user, final String jwtToken) {
    //     final var token = Token.builder()
    //             .user(user)
    //             .token(jwtToken)
    //             .tokenType(TokenType.BEARER)
    //             .revoked(false)
    //             .expired(false)
    //             .build();
    //
    //     tokenRepository.save(token);
    // }
    //
    // public void refreshToken(
    //         final HttpServletRequest request,
    //         final HttpServletResponse response) throws IOException {
    //
    //     final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    //     final String refreshToken;
    //     final String login;
    //
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return;
    //     }
    //
    //     refreshToken = authHeader.substring(7);
    //     login = jwtService.extractLogin(refreshToken);
    //
    //     if (login != null) {
    //         final var user = this.repository.findByLogin(login).orElseThrow();
    //         if (jwtService.isTokenValid(refreshToken, user)) {
    //             final var accessToken = jwtService.generateToken(user);
    //             revokeAllUsersTokens(user);
    //             saveUserToken(user, accessToken);
    //             final var authResponse = AuthenticationResponse.builder()
    //                     .accessToken(accessToken)
    //                     .refreshToken(refreshToken)
    //                     .mfaEnabled(false)
    //                     .build();
    //
    //             new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    //         }
    //     }
    // }
    //
    // public AuthenticationResponse verifyCode(
    //         final VerificationRequest verificationRequest
    // ) {
    //     final UserDemo user = repository
    //             .findByLogin(verificationRequest.getLogin())
    //             .orElseThrow(() -> new EntityNotFoundException(
    //                     String.format("No user found with login: %s", verificationRequest.getLogin()))
    //             );
    //     if (tfaService.isOtpNotValid(user.getSecret(), verificationRequest.getCode())) {
    //         throw new BadCredentialsException("Code is not valid");
    //     }
    //     final var jwtToken = jwtService.generateToken(user);
    //
    //     return AuthenticationResponse.builder()
    //             .accessToken(jwtToken)
    //             .mfaEnabled(user.isMfa_enabled())
    //             .build();
    // }
}
