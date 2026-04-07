package com.invoicesync.modules.company.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.invoicesync.core.common.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Represents a company entity with identification and address details.
 * A company is the core accounting unit in the system. All accounting operations
 * (such as invoices and receipts) are linked to a specific company
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company", schema = "invoice_sync")
public class Company extends BaseEntity {

  /**
   * Company name
   */
  private String name;

  /**
   * City where the company's registered office is located.
   */
  private String city;

  /**
   * Street name (without street number) of the company's registered office.
   * Example: "Hlavná".
   */
  private String street;

  /**
   * Street number of the company's registered office.
   * Example: Example: "25/A".
   */
  @Column(name = "street_number")
  private String streetNumber;

  /**
   * Postal code (ZIP) of the city where the company is located.
   * Example: "821 09".
   */
  private String zip;

  /**
   * Registration number (IČO) – unique government-issued company identifier in the country.
   * Example: "12345678".
   */
  @Column(name = "registration_number")
  private String registrationNumber;

  /**
   * Tax identification number (DIČ) – identifier for tax purposes in the country.
   * Example: "2021234567".
   */
  @Column(name = "tax_id")
  private String taxId;

  /**
   * Value-added tax identification number (IČ DPH).
   * Usually the Tax id prefixed with a country code (e.g., "SK2021234567").
   */
  @Column(name = "vat_id")
  private String vatId;

  @Column(name = "cash_receipt_number")
  private String cashReceiptNumber;

  @Column(name = "card_receipt_number")
  private String cardReceiptNumber;

  /**
   * Email address of the business partner associated with the company.
   * This email is used for sending invoices and other communications.
   */
  @Column(name = "recipient_email")
  private String recipientEmail;

  // @ManyToOne
  // @JoinColumn(name = "\"user_id\"")
  // private UserDemo user;

}
