package com.invoicesync.core.utils;

import com.invoicesync.config.StripeConfig;
import com.invoicesync.core.enums.SubscriptionPlan;

public final class SubscriptionUtil {

  private static final StripeConfig stripePriceConfig = new StripeConfig();

  public static SubscriptionPlan fromStripePriceId(final String priceId) {

    for (final SubscriptionPlan plan : SubscriptionPlan.values()) {
      final String configuredPriceId = stripePriceConfig.getPriceIdForPlan(plan);

      if (configuredPriceId != null && configuredPriceId.equals(priceId)) {
        return plan;
      }
    }
    return null;
  }

}
