package com.invoicesync.dto.invoice.response;

import java.util.Date;

import com.invoicesync.dto.identity.PartnerDto;
import com.invoicesync.dto.invoice.pohoda.InvoiceResponseDetailsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceImportResponseDto {

  private Long id;

  private Date importDate;

  private InvoiceResponseDetailsDTO invoiceDetails;

  private PartnerDto partner;

  private String status;

  private Long company;

}
