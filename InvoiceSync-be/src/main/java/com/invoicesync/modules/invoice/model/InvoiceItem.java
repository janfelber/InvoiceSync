package com.invoicesync.modules.invoice.model;

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

/**
 * Invoice item entity representing a single product or service on a
 * {@link com.invoicesync.modules.invoice.model.Invoice}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice_item", schema = "invoice_sync")
public class InvoiceItem {

  /**
   * Unique ID of this invoice item.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Invoice to which this item belongs.
   */
  @ManyToOne
  @JoinColumn(name = "invoice_id", nullable = false)
  private Invoice invoice;

  /**
   * Name of the product or service.
   */
  private String name;

  /**
   * Quantity of the product or service.
   */
  private int quantity;

  /**
   * Unit type of the product or service(e.g., pcs, kg).
   */
  @Column(name = "unit_type")
  private String unitType;

  /**
   * VAT rate of the item(e.g., 19, 23, 20).
   */
  @Column(name = "vat_rate")
  private int vatRate;

  /**
   * Price per unit, excluding VAT.
   */
  @Column(name = "unit_price_without_vat")
  private BigDecimal unitPriceWithoutVat;

  /**
   * Price per unit, including VAT.
   */
  @Column(name = "unit_price_with_vat")
  private BigDecimal unitPriceWithVat;

  /**
   * Total price of item, excluding VAT.
   */
  @Column(name = "total_item_price_without_vat")
  private BigDecimal totalItemPriceWithoutVat;

  /**
   * Total price of item, including VAT.
   */
  @Column(name = "total_item_price_with_vat")
  private BigDecimal totalItemPriceWithVat;

  /**
   * Accounting code for the item(e.g., 431, 562, 211).
   */
  @Column(name = "account_value")
  private String accountValue;

  /**
   * Description or note for accounting purposes.
   */
  @Column(name = "account_text")
  private String accountText;

}
