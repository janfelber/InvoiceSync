package com.invoicesync.modules.receipt.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.invoicesync.core.common.BaseEntity;
import com.invoicesync.core.enums.InvoiceStatus;
import com.invoicesync.core.enums.PaymentType;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.document.model.ReceiptDocument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Receipt entity representing a different type of accounting document.
 * Each receipt belongs to a {@link Company}, contains {@link ReceiptItem}s
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "receipt", schema = "invoice_sync")
public class Receipt extends BaseEntity {

  /**
   *
   * Unique, incrementing number of the receipt within the company.
   * This number must change for every export of new receipt to ensure it can be imported
   * into external accounting software (e.g., Pohoda) without conflicts.
   * Static or duplicate numbers are not allowed.
   * Each company can configure how receipt numbering works
   */
  @Column(name = "receipt_number")
  private String receiptNumber;

  /**
   * User-facing sequential import number. Increments per user on each receipt import.
   * Assigned atomically via {@code user_receipt_counter} table to prevent duplicates.
   * Once assigned, this number is permanent — it does not change if other receipts are deleted,
   * making it a stable reference for support and audit purposes.
   */
  @Column(name = "receipt_order")
  private Long receiptOrder;

  // @ManyToOne
  // @JoinColumn(name = "\"user_id\"")
  // private UserDemo user;

  /**
   * Company to which the receipt belongs.
   */
  @ManyToOne
  @JoinColumn(name = "\"company\"")
  private Company company;

  /**
   * Line items (products or services) of the receipt.
   */
  @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReceiptItem> items = new ArrayList<>();

  /**
   * Attached documents related to the receipt (e.g., scans, PDFs).
   */
  @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReceiptDocument> documents = new ArrayList<>();

  /**
   * Date when receipt was created.
   */
  private String date;

  /**
   * Date of payment.
   */
  @Column(name = "date_payment")
  private String paymentDate;

  /**
   * Date relevant for tax purposes.
   */
  @Column(name = "date_tax")
  private String taxDate;

  /**
   * Business partner's name.
   */
  @Column(name = "partner_name")
  private String supplierName;

  /**
   * Business partner's city.
   */
  @Column(name = "partner_city")
  private String supplierCity;

  /**
   * Business partner's street.
   */
  @Column(name = "partner_street")
  private String supplierStreet;

  /**
   * Business partner's postal code.
   */
  @Column(name = "partner_zip")
  private String supplierZip;

  /**
   * Business partner's registration number (IČO).
   */
  @Column(name = "partner_registration_number")
  private String supplierRegistrationNumber;

  /**
   * Business partner's tax identification number (DIČ).
   */
  @Column(name = "partner_tax_id")
  private String supplierTaxId;

  /**
   * Business partner's VAT identification number (IČ DPH).
   */
  @Column(name = "partner_vat_id")
  private String supplierVatId;

  /**
   * Date of import.
   */
  @Column(name = "import_date")
  private Date importDate;

  /**
   * Total price of the receipt (including VAT if applicable).
   */
  @Column(name = "total_price_with_vat")
  private BigDecimal totalPriceWithVat;

  /**
   * Total price of the receipt (excluding VAT if applicable).
   */
  @Column(name = "total_price_without_vat")
  private BigDecimal totalPriceWithoutVat;

  /**
   *
   */
  @Column(name = "accounting")
  private String accountValue;

  /**
   *
   */
  @Column(name = "classification_vat")
  private String vatClassification;

  /**
   *
   */
  @Column(name = "classification_kv_vat")
  private String kvVatClassification;

  /**
   * Description or note about the receipt.
   */
  @Column(name = "description")
  private String description;

  /**
   * Indicates if the receipt was paid by credit card.
   */
  @Column(name = "payment_type")
  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;

  /**
   * Processing status of the invoice within the accounting workflow.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "receipt_status")
  private InvoiceStatus status;

}
