package com.invoicesync.modules.invoice.model;

import java.time.LocalDateTime;
import java.util.List;

import com.invoicesync.shared.AccountingLineItem;
import com.invoicesync.shared.MonetaryAmount;
import com.invoicesync.shared.OrganizationDto;

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

  private MonetaryAmount totalAmount;

  private OrganizationDto supplier;

  private List<AccountingLineItem> items;

  private OrganizationDto targetCompany;

}
