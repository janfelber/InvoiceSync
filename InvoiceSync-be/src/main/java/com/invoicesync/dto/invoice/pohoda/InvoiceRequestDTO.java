package com.invoicesync.dto.invoice.pohoda;

import java.util.List;

import com.invoicesync.dto.identity.MyIdentityDTO;
import com.invoicesync.dto.identity.PartnerDTO;

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

  private PartnerDTO partner;

  private List<InvoiceItemDTO> items;

  private MyIdentityDTO myIdentity;

}
