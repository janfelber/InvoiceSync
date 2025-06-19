package com.invoicesync.subscription;

import static com.invoicesync.subscription.SubscriptionLimits.*;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPlan {

  NONE("none", NONE_INVOICE_EXPORT_LIMIT, NONE_RECEIPT_EXPORT_LIMIT,
      NONE_INVOICE_CREATE_LIMIT, 0, List.of()),

  FREE("price_1RLVJ7LJ07OMo5e7HwbkdzmF", FREE_INVOICE_EXPORT_LIMIT, FREE_RECEIPT_EXPORT_LIMIT,
      FREE_INVOICE_CREATE_LIMIT, 0, List.of(
      "50 faktúr mesačne",
      "Základné funkcie"
  )),
  ESSENTIALS("price_1RLNmZLJ07OMo5e7zw4IHUEW", ESSENTIALS_INVOICE_EXPORT_LIMIT, ESSENTIALS_RECEIPT_EXPORT_LIMIT,
      ESSENTIALS_INVOICE_CREATE_LIMIT, 6.99, List.of(
      "150 faktúr mesačne",
      "Základné funkcie",
      "Export údajov do Excelu"
  )),
  PRO("price_1RLVHJLJ07OMo5e7lfYnpV4l", PRO_INVOICE_EXPORT_LIMIT, PRO_RECEIPT_EXPORT_LIMIT, PRO_INVOICE_CREATE_LIMIT,
      19.99, List.of(
      "500 faktúr mesačne",
      "Export údajov do Excelu",
      "Automatické ukladanie dát"
  )),

  ENTERPRISE("price_1RLVJ7LJ07OMo5e7RXYZxyz", ENTERPRISE_INVOICE_EXPORT_LIMIT, ENTERPRISE_RECEIPT_EXPORT_LIMIT,
      ENTERPRISE_INVOICE_CREATE_LIMIT, 79.99, List.of(
      "2000 faktúr mesačne",
      "API prístup",
      "Individuálna podpora",
      "Export údajov do Excelu",
      "Automatické ukladanie dát"
  ));

  private final String priceId;

  private final int monthlyInvoiceExportLimit;

  private final int monthlyReceiptExportLimit;

  private final int monthlyInvoiceCreateLimit;

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
