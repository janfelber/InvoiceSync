package com.invoicesync.supplier.cz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AresClientConfig {

  @Bean("aresWebClient")
  public WebClient aresWebClient() {
    return WebClient.builder().baseUrl("https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty")
        .build();
  }

}
