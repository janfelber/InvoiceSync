package com.invoicesync.auth;

import com.invoicesync.config.JwtService;
import com.invoicesync.repository.UserCredentialRepository;
import com.invoicesync.token.Token;
import com.invoicesync.token.TokenRepository;
import com.invoicesync.token.TokenType;
import com.invoicesync.user.UserDemo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserCredentialRepository repository;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(final RegisterRequest request) {
        final var user = UserDemo.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .login(request.getLogin())
                .role(request.getRole())
                .build();
        final var savedUser = repository.save(user);
        final var jwtToken = jwtService.generateToken(user);

        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(final AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getLogin(),
                request.getPassword())
        );
        final var user = repository.findByLogin(request.getLogin())
                .orElseThrow();
        final var jwtToken = jwtService.generateToken(user);

        revokeAllUsersTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }


    private void revokeAllUsersTokens(final UserDemo user) {
        final var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(final UserDemo user, final String jwtToken) {
        final var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
    }


}
