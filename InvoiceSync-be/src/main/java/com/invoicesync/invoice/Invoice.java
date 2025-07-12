package com.invoicesync.invoice;

import com.invoicesync.company.Company;
import com.invoicesync.shared.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice", schema = "invoice_sync")
public class Invoice extends BaseEntity {

  @Column(name = "invoice_number")
  private String invoiceNumber;

  @ManyToOne
  @JoinColumn(name = "\"company\"")
  private Company company;

  @Column(name = "issue_date")
  private String issueDate;

  @Column(name = "tax_date")
  private String taxDate;

  @Column(name = "accounting_date")
  private String accountingDate;

  @Column(name = "due_date")
  private String dueDate;

  @Column(name = "variable_symbol")
  private String variableSymbol;

  @Column(name = "partner_name")
  private String partnerName;

  @Column(name = "partner_street")
  private String partnerStreet;

  @Column(name = "partner_city")
  private String partnerCity;

  @Column(name = "partner_zip")
  private String partnerZip;

  @Column(name = "partner_registration_number")
  private String partnerRegistrationNumber;

  @Column(name = "partner_tax_id")
  private String partnerTaxId;

  @Column(name = "partner_vat_id")
  private String partnerVatId;

  @Column(name = "status")
  private String status;

  @Column(name = "invoice_type")
  private String invoiceType;

}
