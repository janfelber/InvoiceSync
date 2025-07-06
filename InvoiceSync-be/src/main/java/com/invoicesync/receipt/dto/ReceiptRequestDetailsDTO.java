package com.invoicesync.receipt.dto;

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

  private boolean isPaidByCard;

  private String dateTax;

  private String accountValue;

  private String classificationVAT;

  private String classificationKVVAT;

  private String description;

}
