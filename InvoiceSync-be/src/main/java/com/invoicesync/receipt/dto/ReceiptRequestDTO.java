package com.invoicesync.receipt.dto;

import java.util.List;

import com.invoicesync.shared.dto.identity.MyIdentityDTO;
import com.invoicesync.shared.dto.identity.PartnerDto;

public record ReceiptRequestDTO(
    ReceiptRequestDetailsDTO receiptDetails,

    PartnerDto partner,

    MyIdentityDTO myIdentity,
    List<ReceiptItemDto> items
) {

  public boolean isPaidByCard() {
    return receiptDetails != null && receiptDetails.isPaidByCard();
  }
}
