package com.invoicesync.invoice.dto;

import java.util.List;

import com.invoicesync.shared.dto.identity.MyIdentityDTO;
import com.invoicesync.shared.dto.identity.PartnerDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequestDTO {

  private InvoiceRequestDetailsDTO invoiceDetails;

  private PartnerDto partner;

  private List<InvoiceItemDTO> items;

  private MyIdentityDTO myIdentity;

}
