package com.invoicesync.dto.receipt.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptResponseDetailsDTO {

  private String date;

  private String datePayment;

  private String dateTax;

  private String totalPrice;

}
