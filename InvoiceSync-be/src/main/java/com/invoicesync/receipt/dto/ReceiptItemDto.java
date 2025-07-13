package com.invoicesync.receipt.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItemDto {

  private Long id;

  private String name;

  private String accountText;

  private int quantity;

  private BigDecimal priceWithoutVAT;

  private int vatRate;

  private BigDecimal priceWithVAT;

  private String accountValue;

}
