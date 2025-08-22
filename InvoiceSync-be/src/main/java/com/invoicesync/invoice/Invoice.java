package com.invoicesync.invoice;

import java.util.ArrayList;
import java.util.List;

import com.invoicesync.company.Company;
import com.invoicesync.shared.common.BaseEntity;

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

  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InvoiceItem> items = new ArrayList<>();

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

  @Enumerated(EnumType.STRING)
  @Column(name = "invoice_status")
  private InvoiceStatus status;

  @Column(name = "invoice_type")
  private String invoiceType;

}
