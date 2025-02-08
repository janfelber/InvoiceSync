package com.invoicesync.dto.invoice;

import java.util.Date;

import com.invoicesync.dto.identity.PartnerDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceImportResponseDto {

  private Long id;

  private Date importDate;

  private InvoiceResponseDetailsDTO invoiceDetails;

  private PartnerDTO partner;

  private String status;

  private Long company;

}
