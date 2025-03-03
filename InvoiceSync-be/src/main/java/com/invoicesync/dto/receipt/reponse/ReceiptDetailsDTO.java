package com.invoicesync.dto.receipt.reponse;

import java.util.List;

import com.invoicesync.dto.identity.PartnerDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptItemDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDetailsDTO {

  private Long id;

  private ReceiptResponseDetailsDTO receiptDetails;

  private PartnerDTO partner;

  private List<ReceiptItemDTO> items;

  private Long company;

}
