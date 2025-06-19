package com.invoicesync.invoice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemDTO {

  private BigDecimal quantity;

  private BigDecimal unitPrice;

  private BigDecimal price;

  private BigDecimal priceVAT;

  private BigDecimal priceSum;

  private String accountValue;

}
