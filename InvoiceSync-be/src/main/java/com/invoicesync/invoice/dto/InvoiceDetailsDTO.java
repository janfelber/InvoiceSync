package com.invoicesync.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailsDTO {

  private String invoiceNumber;

  private String variableSymbol;

  private String issueDate;

  private String taxDate;

  private String accountingDate;

  private String dueDate;

  private String status;

  private String invoiceType;

}
