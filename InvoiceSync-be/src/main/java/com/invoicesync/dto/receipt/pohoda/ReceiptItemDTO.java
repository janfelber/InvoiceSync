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

  private String name;

  private int quantity;

  private BigDecimal unitPrice;

  private BigDecimal price;

  private int vatRate;

  private BigDecimal priceSum;

}
