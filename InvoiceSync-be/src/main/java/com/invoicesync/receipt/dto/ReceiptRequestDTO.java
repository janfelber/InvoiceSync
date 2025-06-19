package com.invoicesync.receipt.dto;

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
public class ReceiptRequestDTO {

  private ReceiptRequestDetailsDTO receiptDetails;

  private PartnerDto partner;

  private MyIdentityDTO myIdentity;

  private List<ReceiptItemDto> items;

}
