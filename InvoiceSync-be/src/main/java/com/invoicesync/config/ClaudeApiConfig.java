package com.invoicesync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClaudeApiConfig {

  private static final String API_URL = "https://api.anthropic.com/v1/messages";

  private static final String ANTHROPIC_VERSION = "2023-06-01";

  @Value("${anthropic.api.key}")
  private String apiKey;

  @Value("${anthropic.api.model}")
  private String model;

  public String getApiKey() {
    return apiKey;
  }

  public String getModel() {
    return model;
  }

  public String getApiUrl() {
    return API_URL;
  }

  public String getAnthropicVersion() {
    return ANTHROPIC_VERSION;
  }

}
