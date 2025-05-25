package com.invoicesync.dto.receipt.reponse;

import java.time.LocalDateTime;
import java.util.List;

import com.invoicesync.dto.identity.MyIdentityDTO;
import com.invoicesync.dto.identity.PartnerDto;
import com.invoicesync.dto.receipt.pohoda.ReceiptItemDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDetailDto {
  private Long id;

  private LocalDateTime createdAt;

  private DetailDto receiptDetails;

  private PartnerDto partner;

  private List<ReceiptItemDto> items;

  private MyIdentityDTO company;

}
