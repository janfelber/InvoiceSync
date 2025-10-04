package com.invoicesync.receipt.receiptItem;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SplitPartDTO {
  private String name;
  private int quantity;
  private String splitPercentage;
  private BigDecimal priceWithVAT;
  private BigDecimal priceWithoutVAT;
  private int vatRate;
}
