package com.invoicesync.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EkasaClientConfig {

  @Bean(name = "ekasaWebClient")
  public WebClient ekasaWebClient() {
    return WebClient.builder().baseUrl("https://ekasa.financnasprava.sk/mdu/api/v1/opd/receipt/find").build();
  }

  @Bean(name = "openAiWebClient")
  public WebClient openAiWebClient(final WebClient.Builder builder) {
    return builder.baseUrl("https://api.openai.com/v1").build();
  }

}
