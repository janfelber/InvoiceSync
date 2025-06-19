package com.invoicesync.invoice.dto;

import java.util.Date;

import com.invoicesync.shared.dto.identity.PartnerDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceResponseDTO {

  private Long id;

  private Date importDate;

  private InvoiceResponseDetailsDTO invoiceDetails;

  private PartnerDto partner;

  private String status;

  private Long company;

}
