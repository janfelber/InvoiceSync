package com.invoicesync.core.enums;

import static com.invoicesync.modules.subscription.model.SubscriptionLimits.*;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPlan {

  NONE("none", NONE_INVOICE_EXPORT_LIMIT, NONE_RECEIPT_EXPORT_LIMIT,
      NONE_INVOICE_CREATE_LIMIT, 0, List.of()),

  FREE("free", FREE_INVOICE_EXPORT_LIMIT, FREE_RECEIPT_EXPORT_LIMIT,
      FREE_INVOICE_CREATE_LIMIT, 0, List.of(
      "20 spracovaní faktúr mesačne",
      "20 spracovaní bločkov mesačne",
      "10 vytvorených faktúr mesačne",
      "Export (Pohoda, Omega)"
  )),
  ESSENTIALS("essentials", ESSENTIALS_INVOICE_EXPORT_LIMIT, ESSENTIALS_RECEIPT_EXPORT_LIMIT,
      ESSENTIALS_INVOICE_CREATE_LIMIT, 6.99, List.of(
      "150 spracovaní faktúr mesačne",
      "50 spracovaní bločkov mesačne",
      "50 vytvorených faktúr mesačne",
      "Export (Pohoda, Omega)"
  )),
  PRO("pro", PRO_INVOICE_EXPORT_LIMIT, PRO_RECEIPT_EXPORT_LIMIT, PRO_INVOICE_CREATE_LIMIT,
      19.99, List.of(
      "500 spracovaní faktúr mesačne",
      "150 spracovaní bločkov mesačne",
      "150 vytvorených faktúr mesačne",
      "Export (Pohoda, Omega)"
  )),

  ENTERPRISE("enterprise", ENTERPRISE_INVOICE_EXPORT_LIMIT, ENTERPRISE_RECEIPT_EXPORT_LIMIT,
      ENTERPRISE_INVOICE_CREATE_LIMIT, 79.99, List.of(
      "2000+ spracovaní faktúr mesačne",
      "1000+ spracovaní bločkov mesačne",
      "Neobmedzený počet faktúr mesačne",
      "Export (Pohoda, Omega)"
  ));

  private final String priceId;

  private final int monthlyInvoiceExportLimit;

  private final int monthlyReceiptExportLimit;

  private final int monthlyInvoiceCreateLimit;

  private final double monthlyPrice;

  private final List<String> features;
}
