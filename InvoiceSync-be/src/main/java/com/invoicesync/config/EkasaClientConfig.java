package com.invoicesync.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.common.net.HttpHeaders;

@Configuration
public class EkasaClientConfig {

  @Bean(name = "ekasaWebClient")
  public WebClient ekasaWebClient() {
    return WebClient.builder().baseUrl("https://ekasa.financnasprava.sk/mdu/api/v1/opd/receipt/find").build();
  }

  @Bean
  @Qualifier("openAiWebClient")
  public WebClient openAiWebClient(
      @Value("${openai.api.key}") String apiKey
  ) {
    return WebClient.builder()
        .baseUrl("https://api.openai.com/v1")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

}
