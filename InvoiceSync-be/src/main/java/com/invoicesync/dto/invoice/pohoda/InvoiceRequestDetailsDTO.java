package com.invoicesync.dto.invoice.pohoda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequestDetailsDTO {

  private String invoiceType;

  private String invoiceNumber;

  private String variableSymbol;

  private String pairingSymbol;

  private String dateIssue;

  private String dateTax;

  private String dateDue;

  private String dateAccounting;

}
