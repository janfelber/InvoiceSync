package com.invoicesync.modules.invoice.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice_vat_breakdown", schema = "invoice_sync")
public class InvoiceVatBreakdown {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Invoice to which vat belongs.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invoice_id", nullable = false)
  private Invoice invoice;

  /**
   * VAT rate percentage this row summarizes (e.g. 23, 5, 0). One row per distinct rate on the invoice.
   */
  @Column(name = "vat_rate", nullable = false)
  private Integer vatRate;

  /**
   * Sum of the invoice amount at this VAT rate, without VAT.
   */
  @Column(name = "sum_without_vat", nullable = false)
  private BigDecimal sumWithoutVAT;

  /**
   * Sum of the invoice amount at this VAT rate, with VAT.
   */
  @Column(name = "sum_with_vat", nullable = false)
  private BigDecimal sumWithVAT;

  /**
   * VAT amount at this rate (sumWithVAT - sumWithoutVAT).
   */
  @Column(name = "sum_vat", nullable = false)
  private BigDecimal sumVAT;

}
