package com.invoicesync.dto.receipt.pohoda;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDTO {

  private String partnerName;

  private String partnerCity;

  private String partnerStreet;

  private String partnerZip;

  private String date;

  private String datePayment;

  private String dateTax;

  private String partnerRegistrationNumber;

  private String partnerTaxId;

  private String partnerVatId;

  private String totalPrice;

  private List<ReceiptItemDTO> items;

}
