package com.invoicesync.modules.receipt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailDto {

  private String date;

  private String datePayment;

  private String dateTax;

  private String totalPrice;

  private String accountValue;

  private boolean isPaidByCard;

  private String classificationVAT;

  private String classificationKVVAT;

  private String description;

}
