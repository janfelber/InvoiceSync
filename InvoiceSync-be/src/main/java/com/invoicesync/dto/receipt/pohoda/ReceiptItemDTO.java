package com.invoicesync.dto.receipt.pohoda;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItemDTO {

  private String text;

  private BigDecimal quantity;

  private BigDecimal unitPrice;

  private BigDecimal price;

  private BigDecimal priceVAT;

  private BigDecimal priceSum;

}
