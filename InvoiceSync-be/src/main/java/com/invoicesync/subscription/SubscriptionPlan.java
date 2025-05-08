package com.invoicesync.subscription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPlan {

  FREE("price_1RLVJ7LJ07OMo5e7HwbkdzmF", 50),
  ESSENTIALS("price_1RLNmZLJ07OMo5e7zw4IHUEW", 100),
  PRO("price_1RLVHJLJ07OMo5e7lfYnpV4l", 500),
  ENTERPRISE("price_1RLVJ7LJ07OMo5e7RXYZxyz", 2000);

  private final String priceId;
  private final int monthlyInvoiceLimit;

  // Metóda na získanie SubscriptionPlan na základe priceId
  public static SubscriptionPlan fromStripePriceId(String priceId) {
    for (SubscriptionPlan plan : SubscriptionPlan.values()) {
      if (plan.priceId.equals(priceId)) {
        return plan;
      }
    }
    return null;  // Ak nenájdeš žiadny plán, vráti null
  }
}
