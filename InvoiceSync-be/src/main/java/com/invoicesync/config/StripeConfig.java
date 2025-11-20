package com.invoicesync.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.invoicesync.core.enums.SubscriptionPlan;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "stripe.price")
@Getter
@Setter
public class StripeConfig {

  private String free;
  private String essentials;
  private String pro;
  private String enterprise;

  public String getPriceIdForPlan(final SubscriptionPlan plan) {
    return switch (plan) {
      case FREE -> free;
      case ESSENTIALS -> essentials;
      case PRO -> pro;
      case ENTERPRISE -> enterprise;
      default -> null;
    };
  }
}
