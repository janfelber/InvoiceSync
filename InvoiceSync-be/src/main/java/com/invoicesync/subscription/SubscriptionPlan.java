package com.invoicesync.subscription;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPlan {

  NONE("none", 0, 0, List.of()),

  FREE("price_1RLVJ7LJ07OMo5e7HwbkdzmF", 50, 0, List.of(
      "50 faktúr mesačne",
      "Základné funkcie"
  )),
  ESSENTIALS("price_1RLNmZLJ07OMo5e7zw4IHUEW", 150, 6.99, List.of(
      "150 faktúr mesačne",
      "Základné funkcie",
      "Export údajov do Excelu"
  )),
  PRO("price_1RLVHJLJ07OMo5e7lfYnpV4l", 500, 19.99, List.of(
      "500 faktúr mesačne",
      "Export údajov do Excelu",
      "Automatické ukladanie dát"
  )),

  ENTERPRISE("price_1RLVJ7LJ07OMo5e7RXYZxyz", 2000, 79.99, List.of(
      "2000 faktúr mesačne",
      "API prístup",
      "Individuálna podpora",
      "Export údajov do Excelu",
      "Automatické ukladanie dát"
  ));

  private final String priceId;

  private final int monthlyInvoiceLimit;

  private final double monthlyPrice;

  private final List<String> features;

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
