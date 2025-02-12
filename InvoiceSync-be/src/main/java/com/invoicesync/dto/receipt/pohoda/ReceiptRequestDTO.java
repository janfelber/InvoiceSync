package com.invoicesync.dto.receipt.pohoda;

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
public class ReceiptRequestDTO {

  private ReceiptRequestDetailsDTO receiptDetails;

  private PartnerDTO partner;

  private MyIdentityDTO myIdentity;

  private List<ReceiptItemDTO> items;

}
