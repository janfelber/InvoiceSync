  package com.invoicesync.module;

  import java.math.BigDecimal;
  import java.math.BigInteger;
  import java.time.LocalDateTime;
  import java.util.Date;

  import com.invoicesync.module.UserCredential;

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
  @Table(name = "\"INVOICE_IMPORT\"", schema = "invoice_sync")
  public class InvoiceImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private int id;

    @ManyToOne()
    @JoinColumn(name = "\"USER_ID\"")
    private UserCredential userCredential;


    @Column(name = "\"INVOICE_NUMBER\"")
    private String invoiceNumber;


    @Column(name = "\"INVOICE_IMPORT_DATE\"")
    private LocalDateTime invoiceImportDate;

    @Column(name = "\"INVOICE_ISSUE_DATE\"")
    private String invoiceIssueDate;


    @Column(name = "\"INVOICE_DELIVERY_DATE\"")
    private String invoiceDeliveryDate;

    @Column(name = "\"INVOICE_DUE_DATE\"")
    private String invoiceDueDate;


    @Column(name = "\"INVOICE_VARIABLE_SYMBOL\"")
    private String invoiceVariableSymbol;


    @Column(name = "\"INVOICE_TOTAL_AMOUNT\"")
    private BigDecimal invoiceAmount;


    @Column(name = "\"COMPANY_NAME\"")
    private String companyName;


    @Column(name = "\"COMPANY_CITY\"")
    private String companyCity;


    @Column(name = "\"COMPANY_ADDRESS\"")
    private String companyAddress;


    @Column(name = "\"COMPANY_POSTAL_CODE\"")
    private String companyPostalCode;


    @Column(name = "\"COMPANY_VAT_NUMBER\"")
    private String companyVatNumber;


    @Column(name = "\"COMPANY_IBAN\"")
    private String companyIban;


    @Column(name = "\"COMPANY_REGISTRATION_NUMBER\"")
    private String companyRegistrationNumber;


    @Column(name = "\"COMPANY_TAX_NUMBER\"")
    private String companyTaxNumber;


    @Column(name = "\"INVOICE_STATUS\"")
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

    public String getInvoiceNumber() {
      return invoiceNumber;
    }

    public void setInvoiceNumber(
        final  String invoiceNumber) {
      this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getInvoiceImportDate() {
      return invoiceImportDate;
    }

    public void setInvoiceImportDate(
        final LocalDateTime invoiceImportDate) {
      this.invoiceImportDate = invoiceImportDate;
    }

    public  String getInvoiceIssueDate() {
      return invoiceIssueDate;
    }

    public void setInvoiceIssueDate(
        final  String invoiceIssueDate) {
      this.invoiceIssueDate = invoiceIssueDate;
    }

    public  String getInvoiceDeliveryDate() {
      return invoiceDeliveryDate;
    }

    public void setInvoiceDeliveryDate(
        final  String invoiceDeliveryDate) {
      this.invoiceDeliveryDate = invoiceDeliveryDate;
    }

    public String getInvoiceDueDate() {
      return invoiceDueDate;
    }

    public void setInvoiceDueDate(
        final String invoiceDueDate) {
      this.invoiceDueDate = invoiceDueDate;
    }

    public  String getInvoiceVariableSymbol() {
      return invoiceVariableSymbol;
    }

    public void setInvoiceVariableSymbol(
        final  String invoiceVariableSymbol) {
      this.invoiceVariableSymbol = invoiceVariableSymbol;
    }

    public  BigDecimal getInvoiceAmount() {
      return invoiceAmount;
    }

    public void setInvoiceAmount(
        final   BigDecimal invoiceAmount) {
      this.invoiceAmount = invoiceAmount;
    }

    public   String getCompanyName() {
      return companyName;
    }

    public void setCompanyName(
        final   String companyName) {
      this.companyName = companyName;
    }

    public   String getCompanyCity() {
      return companyCity;
    }

    public void setCompanyCity(
        final   String companyCity) {
      this.companyCity = companyCity;
    }

    public   String getCompanyAddress() {
      return companyAddress;
    }

    public void setCompanyAddress(
        final   String companyAddress) {
      this.companyAddress = companyAddress;
    }

    public   String getCompanyPostalCode() {
      return companyPostalCode;
    }

    public void setCompanyPostalCode(
        final   String companyPostalCode) {
      this.companyPostalCode = companyPostalCode;
    }

    public  String getCompanyVatNumber() {
      return companyVatNumber;
    }

    public void setCompanyVatNumber(
        final String companyVatNumber) {
      this.companyVatNumber = companyVatNumber;
    }

    public String getCompanyIban() {
      return companyIban;
    }

    public void setCompanyIban(
        final  String companyIban) {
      this.companyIban = companyIban;
    }

    public  String getCompanyRegistrationNumber() {
      return companyRegistrationNumber;
    }

    public void setCompanyRegistrationNumber(
        final  String companyRegistrationNumber) {
      this.companyRegistrationNumber = companyRegistrationNumber;
    }

    public  String getCompanyTaxNumber() {
      return companyTaxNumber;
    }

    public void setCompanyTaxNumber(
        final  String companyTaxNumber) {
      this.companyTaxNumber = companyTaxNumber;
    }

    public  String getInvoiceStatus() {
      return invoiceStatus;
    }

    public void setInvoiceStatus(
        final   String invoiceStatus) {
      this.invoiceStatus = invoiceStatus;
    }

  }
