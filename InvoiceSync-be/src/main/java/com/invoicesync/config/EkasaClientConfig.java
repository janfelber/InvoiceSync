package com.invoicesync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EkasaClientConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.builder().baseUrl("https://ekasa.financnasprava.sk/mdu/api/v1/opd/receipt/find").build();
  }

}
