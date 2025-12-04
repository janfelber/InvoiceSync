package com.invoicesync.core.utils;

import org.springframework.stereotype.Component;

import com.invoicesync.config.StripeConfig;
import com.invoicesync.core.enums.SubscriptionPlan;

@Component
public class SubscriptionUtil {

  private final StripeConfig stripeConfig;

  public SubscriptionUtil(final StripeConfig stripeConfig) {
    this.stripeConfig = stripeConfig;
  }

  public SubscriptionPlan fromStripePriceId(final String priceId) {

    for (final SubscriptionPlan plan : SubscriptionPlan.values()) {
      final String configuredPriceId = stripeConfig.getPriceIdForPlan(plan);

      if (configuredPriceId != null && configuredPriceId.equals(priceId)) {
        return plan;
      }
    }
    return null;
  }

}
