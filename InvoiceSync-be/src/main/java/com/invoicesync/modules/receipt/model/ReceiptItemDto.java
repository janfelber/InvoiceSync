package com.invoicesync.modules.receipt.model;

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

  private int quantity;

  private String unitType;

  private int vatRate;

  private BigDecimal unitPriceWithoutVat;

  private BigDecimal unitPriceWithVat;

  private BigDecimal totalItemPriceWithoutVat;

  private BigDecimal totalItemPriceWithVat;

  private String accountValue;

  private String accountText;

}
