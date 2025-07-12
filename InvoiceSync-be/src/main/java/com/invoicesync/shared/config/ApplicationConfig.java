package com.invoicesync.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    // private final UserCredentialRepository userCredentialRepository;
    //
    // @Bean
    // public UserDetailsService userDetailsService() {
    //     return login -> userCredentialRepository.findByLogin(login)
    //             .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    // }

    // @Bean
    // public AuthenticationProvider authenticationProvider() {
    //     final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    //     authProvider.setUserDetailsService(userDetailsService());
    //     authProvider.setPasswordEncoder(passwordEncoder());
    //     return authProvider;
    // }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public Tesseract tesseract() {
        final Tesseract tesseract = new Tesseract();
        // nastav jazyk – napr. slovenčina + angličtina
        tesseract.setLanguage("slk+eng");
        // nastav cestu k traineddata súborom
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata/"); // alebo iná platná cesta
        return tesseract;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
