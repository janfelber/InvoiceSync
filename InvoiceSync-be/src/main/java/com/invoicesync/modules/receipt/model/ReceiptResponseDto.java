package com.invoicesync.modules.receipt.model;

import java.time.LocalDateTime;
import java.util.List;

import com.invoicesync.core.identity.PartnerDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptResponseDto {

  private Long id;

  private Long orderNumber;

  private LocalDateTime createdAt;

  private DetailDto receiptDetails;

  //TODO rename this to supplier
  private PartnerDto partner;

  private List<ReceiptItemDto> items;

  private String companyName;

}
