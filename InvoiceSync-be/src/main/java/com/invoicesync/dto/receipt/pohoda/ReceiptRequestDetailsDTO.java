package com.invoicesync.dto.receipt.pohoda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptRequestDetailsDTO {

  private String numberRequested;

  private String date;

  private String datePayment;

  private String dateTax;

}
