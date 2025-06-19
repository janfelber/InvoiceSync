package com.invoicesync.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  // private final JwtAuthenticationFilter jwtAuthFilter;
  // private final AuthenticationProvider authenticationProvider;
  //   private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                    req -> req.requestMatchers(
                            "/api/v1/auth/**",
                            "/v3/api-docs",
                            "/v3/api-docs/**",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/stripe/webhook",
                            "/stripe/create-checkout-session"
                        )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
            .oauth2ResourceServer(auth ->
                auth.jwt(token -> token.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConvertor())))
        ;
        return http.build();
    }
}
