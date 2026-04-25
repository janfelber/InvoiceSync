package com.invoicesync.modules.receipt.model;

import com.invoicesync.core.enums.PaymentType;

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

  private String totalPriceWithVat;

  private String totalPriceWithoutVat;

  private String accountValue;

  private PaymentType paymentType;

  private String classificationVAT;

  private String classificationKVVAT;

  private String description;

}
