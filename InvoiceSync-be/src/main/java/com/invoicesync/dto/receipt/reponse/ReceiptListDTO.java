package com.invoicesync.dto.receipt.reponse;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptListDTO {

  private Long id;

  private String partnerName;

  private Date importDate;

  private String company;

  private String partnerRegistrationNumber;

  private String partnerTaxId;

  private String partnerVatId;

  private String totalPrice;
}
