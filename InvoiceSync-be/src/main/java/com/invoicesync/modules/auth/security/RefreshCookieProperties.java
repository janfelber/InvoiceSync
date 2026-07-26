package com.invoicesync.modules.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "security.jwt.refresh-cookie")
public class RefreshCookieProperties {

  private String name;

  private String path;

  private boolean secure;

  private String sameSite;

}
