package com.invoicesync.modules.invoice.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.invoicesync.core.common.BaseEntity;
import com.invoicesync.modules.document.model.InvoiceDocument;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.core.enums.InvoiceStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Invoice entity representing an accounting document.
 * Each invoice belongs to a {@link Company}, contains {@link InvoiceItem}s
 * and may have attached {@link InvoiceDocument}s.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice", schema = "invoice_sync")
public class Invoice extends BaseEntity {

  /**
   * Invoice number – unique identifier of the invoice.
   */
  @Column(name = "invoice_number")
  private String invoiceNumber;

  /**
   * Company to which the invoice belongs.
   */
  @ManyToOne
  @JoinColumn(name = "\"company\"")
  private Company company;

  /**
   * Line items (products or services) of the invoice.
   */
  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InvoiceItem> items = new ArrayList<>();

  /**
   * Attached documents related to the invoice (e.g., scans, PDFs).
   */
  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InvoiceDocument> documents = new ArrayList<>();

  /**
   * Date when the invoice was issued.
   */
  @Column(name = "issue_date")
  private String issueDate;

  /**
   * Date relevant for tax purposes.
   */
  @Column(name = "tax_date")
  private String taxDate;

  /**
   * Date when the invoice is recorded in accounting.
   */
  @Column(name = "accounting_date")
  private String accountingDate;

  /**
   * Due date for payment.
   */
  @Column(name = "due_date")
  private String dueDate;

  /**
   * Variable symbol used for payment identification.
   */
  @Column(name = "variable_symbol")
  private String variableSymbol;

  /**
   * Business partner's name.
   */
  @Column(name = "partner_name")
  private String partnerName;

  /**
   * Business partner's city.
   */
  @Column(name = "partner_city")
  private String partnerCity;

  /**
   * Business partner's street.
   */
  @Column(name = "partner_street")
  private String partnerStreet;

  /**
   * Business partner's postal code.
   */
  @Column(name = "partner_zip")
  private String partnerZip;

  /**
   * Business partner's registration number (IČO).
   */
  @Column(name = "partner_registration_number")
  private String partnerRegistrationNumber;

  /**
   * Business partner's tax identification number (DIČ).
   */
  @Column(name = "partner_tax_id")
  private String partnerTaxId;

  /**
   * Business partner's VAT identification number (IČ DPH).
   */
  @Column(name = "partner_vat_id")
  private String partnerVatId;

  /**
   * Processing status of the invoice within the accounting workflow.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "invoice_status")
  private InvoiceStatus status;

  /**
   * Type of the invoice (e.g., issued, received, ...).
   */
  @Column(name = "invoice_type")
  private String invoiceType;

  /**
   * Total price of the invoice, excluding VAT.
   */
  @Column(name = "total_price_without_vat")
  private BigDecimal totalPriceWithoutVat;

  /**
   * Total price of the invoice, including VAT.
   */
  @Column(name = "total_price_with_vat")
  private BigDecimal totalPriceWithVat;

}
