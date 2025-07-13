package com.invoicesync.invoice.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.invoicesync.receipt.dto.ReceiptItemDto;
import com.invoicesync.shared.dto.identity.MyIdentity;
import com.invoicesync.shared.dto.identity.PartnerDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {

  private Long id;

  private LocalDateTime createdAt;

  private InvoiceDetails invoiceDetails;

  private PartnerDto partner;

  private List<ReceiptItemDto> items;

  private MyIdentity myIdentity;

}
