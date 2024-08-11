package com.example.pohodaimport.module;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "INVOICE_IMPORT", schema = "invoice_sync")
public class InvoiceImport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private int id;

  @ManyToOne()
  @JoinColumn(name = "CREDENTIAL_ID")
  private UserCredential userCredential;

  @NotNull
  @NotEmpty
  @Column(name = "INVOICE_NUMBER")
  private String invoiceNumber;

  @NotNull
  @NotEmpty
  @Column(name = "INVOICE_IMPORT_DATE")
  private Date invoiceImportDate;

  @NotNull
  @NotEmpty
  @Column(name = "INVOICE_ISSUE_DATE")
  private Date invoiceIssueDate;

  @NotNull
  @NotEmpty
  @Column(name = "INVOICE_DELIVERY_DATE")
  private Date invoiceDeliveryDate;

  @NotNull
  @NotEmpty
  @Column(name = "INVOICE_DUE_DATE")
  private Date invoiceDueDate;

  @NotNull
  @NotEmpty
  @Column(name = "INVOICE_VARIABLE_SYMBOL")
  private BigInteger invoiceVariableSymbol;

  @NotNull
  @NotEmpty
  @Column(name = "INVOICE_AMOUNT")
  private BigDecimal invoiceAmount;

  @NotNull
  @NotEmpty
  @Column(name = "COMPANY_NAME")
  private String companyName;

  @NotNull
  @NotEmpty
  @Column(name = "COMPANY_CITY")
  private String companyCity;

  @NotNull
  @NotEmpty
  @Column(name = "COMPANY_ADDRESS")
  private String companyAddress;

  @NotNull
  @NotEmpty
  @Column(name = "COMPANY_POSTAL_CODE")
  private String companyPostalCode;

  @NotNull
  @NotEmpty
  @Column(name = "COMPANY_VAT_NUMBER")
  private BigInteger companyVatNumber;

  @NotNull
  @NotEmpty
  @Column(name = "COMPANY_IBAN")
  private BigInteger companyIban;

  @NotNull
  @NotEmpty
  @Column(name = "COMPANY_REGISTRATION_NUMBER")
  private BigInteger companyRegistrationNumber;

  @NotNull
  @NotEmpty
  @Column(name = "COMPANY_TAX_NUMBER")
  private BigInteger companyTaxNumber;

  @NotNull
  @NotEmpty
  @Column(name = "INVOICE_STATUS")
  private String invoiceStatus;

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public UserCredential getUserCredential() {
    return userCredential;
  }

  public void setUserCredential(final UserCredential userCredential) {
    this.userCredential = userCredential;
  }

  public @NotNull @NotEmpty String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(
      final @NotNull @NotEmpty String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public @NotNull @NotEmpty Date getInvoiceImportDate() {
    return invoiceImportDate;
  }

  public void setInvoiceImportDate(
      final @NotNull @NotEmpty Date invoiceImportDate) {
    this.invoiceImportDate = invoiceImportDate;
  }

  public @NotNull @NotEmpty Date getInvoiceIssueDate() {
    return invoiceIssueDate;
  }

  public void setInvoiceIssueDate(
      final @NotNull @NotEmpty Date invoiceIssueDate) {
    this.invoiceIssueDate = invoiceIssueDate;
  }

  public @NotNull @NotEmpty Date getInvoiceDeliveryDate() {
    return invoiceDeliveryDate;
  }

  public void setInvoiceDeliveryDate(
      final @NotNull @NotEmpty Date invoiceDeliveryDate) {
    this.invoiceDeliveryDate = invoiceDeliveryDate;
  }

  public @NotNull @NotEmpty Date getInvoiceDueDate() {
    return invoiceDueDate;
  }

  public void setInvoiceDueDate(
      final @NotNull @NotEmpty Date invoiceDueDate) {
    this.invoiceDueDate = invoiceDueDate;
  }

  public @NotNull @NotEmpty BigInteger getInvoiceVariableSymbol() {
    return invoiceVariableSymbol;
  }

  public void setInvoiceVariableSymbol(
      final @NotNull @NotEmpty BigInteger invoiceVariableSymbol) {
    this.invoiceVariableSymbol = invoiceVariableSymbol;
  }

  public @NotNull @NotEmpty BigDecimal getInvoiceAmount() {
    return invoiceAmount;
  }

  public void setInvoiceAmount(
      final @NotNull @NotEmpty BigDecimal invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public @NotNull @NotEmpty String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(
      final @NotNull @NotEmpty String companyName) {
    this.companyName = companyName;
  }

  public @NotNull @NotEmpty String getCompanyCity() {
    return companyCity;
  }

  public void setCompanyCity(
      final @NotNull @NotEmpty String companyCity) {
    this.companyCity = companyCity;
  }

  public @NotNull @NotEmpty String getCompanyAddress() {
    return companyAddress;
  }

  public void setCompanyAddress(
      final @NotNull @NotEmpty String companyAddress) {
    this.companyAddress = companyAddress;
  }

  public @NotNull @NotEmpty String getCompanyPostalCode() {
    return companyPostalCode;
  }

  public void setCompanyPostalCode(
      final @NotNull @NotEmpty String companyPostalCode) {
    this.companyPostalCode = companyPostalCode;
  }

  public @NotNull @NotEmpty BigInteger getCompanyVatNumber() {
    return companyVatNumber;
  }

  public void setCompanyVatNumber(
      final @NotNull @NotEmpty BigInteger companyVatNumber) {
    this.companyVatNumber = companyVatNumber;
  }

  public @NotNull @NotEmpty BigInteger getCompanyIban() {
    return companyIban;
  }

  public void setCompanyIban(
      final @NotNull @NotEmpty BigInteger companyIban) {
    this.companyIban = companyIban;
  }

  public @NotNull @NotEmpty BigInteger getCompanyRegistrationNumber() {
    return companyRegistrationNumber;
  }

  public void setCompanyRegistrationNumber(
      final @NotNull @NotEmpty BigInteger companyRegistrationNumber) {
    this.companyRegistrationNumber = companyRegistrationNumber;
  }

  public @NotNull @NotEmpty BigInteger getCompanyTaxNumber() {
    return companyTaxNumber;
  }

  public void setCompanyTaxNumber(
      final @NotNull @NotEmpty BigInteger companyTaxNumber) {
    this.companyTaxNumber = companyTaxNumber;
  }

  public @NotNull @NotEmpty String getInvoiceStatus() {
    return invoiceStatus;
  }

  public void setInvoiceStatus(
      final @NotNull @NotEmpty String invoiceStatus) {
    this.invoiceStatus = invoiceStatus;
  }

}
