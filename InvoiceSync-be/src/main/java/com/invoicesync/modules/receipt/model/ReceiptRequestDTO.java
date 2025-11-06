package com.invoicesync.modules.receipt.model;

import java.util.List;

import com.invoicesync.core.identity.MyIdentity;
import com.invoicesync.core.identity.PartnerDto;

public record ReceiptRequestDTO(
    ReceiptRequestDetailsDTO receiptDetails,

    PartnerDto partner,

    MyIdentity myIdentity,
    List<ReceiptItemDto> items
) {

  public boolean isPaidByCard() {
    return receiptDetails != null && receiptDetails.isPaidByCard();
  }
}
