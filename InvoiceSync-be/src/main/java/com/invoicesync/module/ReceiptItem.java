package com.invoicesync.module;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "receipt_item", schema = "invoice_sync")
public class ReceiptItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "receipt_id", nullable = false)
  private Receipt receipt;

  private String name;

  private int quantity;

  @Column(name = "price_without_vat")
  private BigDecimal priceWithoutVAT;

  @Column(name = "vat_rate")
  private int vatRate;

  @Column(name = "account_value")
  private String accountValue;

  @Column(name = "price_with_vat")
  private BigDecimal priceWithVAT;

  @Column(name = "account_text")
  private String accountText;

}
