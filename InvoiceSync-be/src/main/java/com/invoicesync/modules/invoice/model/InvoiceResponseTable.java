package com.invoicesync.modules.invoice.model;

import java.time.LocalDateTime;
import java.util.List;

import com.invoicesync.core.identity.PartnerDto;
import com.invoicesync.modules.receipt.model.ReceiptItemDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseTable {

  private Long id;

  private LocalDateTime createAt;

  private InvoiceDetails invoiceDetails;

  private PartnerDto partner;

  private List<ReceiptItemDto> items;

  private String companyName;

}
