package com.invoicesync.subscription.guard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LimitResponseDTO {

  private int usedLimit;

  private int totalLimit;

  private int invoiceExportLimit;

  private int invoiceExportUsed;

  private int receiptExportLimit;

  private int receiptExportUsed;

  private int invoiceCreateLimit;

  private int invoiceCreateUsed;
}
