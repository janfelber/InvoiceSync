package com.invoicesync.dto.invoice.pohoda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDetailsDTO {

  private String invoiceNumber;

  private String variableSymbol;

  private String pairingSymbol;

  private String issueDate;

  private String taxDate;

  private String dueDate;

}
