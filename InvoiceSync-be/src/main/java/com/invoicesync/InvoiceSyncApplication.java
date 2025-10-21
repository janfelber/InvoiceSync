package com.invoicesync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.invoicesync.auth.AuditorAwareImpl;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class InvoiceSyncApplication {

    public static void main(final String[] args) {
        SpringApplication.run(InvoiceSyncApplication.class, args);
    }

    @Bean
    public AuditorAwareImpl auditorAware() {
        return new AuditorAwareImpl();
    }

    // @Bean
    // public CorsFilter corsFilter() {
    //     final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    //     final CorsConfiguration corsConfiguration = new CorsConfiguration();
    //     corsConfiguration.setAllowCredentials(true);
    //     corsConfiguration.setAllowedOrigins(
    //         Arrays.asList("http://localhost:4200", "https://dex.uctovnictvonitra.sk"));
    //     corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
    //             "Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
    //             "Access-Control-Request-Method", "Access-Control-Request-Headers"));
    //     corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
    //             "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "File-Name"));
    //     corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    //     urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
    //     return new CorsFilter(urlBasedCorsConfigurationSource);
    // }
}
