package com.invoicesync.receipt.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.invoicesync.shared.dto.identity.MyIdentity;
import com.invoicesync.shared.dto.identity.PartnerDto;

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

  private MyIdentity company;

}
