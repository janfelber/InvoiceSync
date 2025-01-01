  package com.invoicesync.module;

  import java.util.Date;

  import com.invoicesync.user.UserDemo;

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
  @Table(name = "invoice", schema = "invoice_sync")
  public class InvoiceImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "\"user_id\"")
    private UserDemo user;

    private String invoice_number;

    private Date invoice_import_date;

    private String invoice_issue_date;

    private String invoice_delivery_date;

    private String invoice_due_date;

    private String invoice_variable_symbol;

    private String invoice_total_amount;

    private String invoice_company_name;

    private String invoice_company_city;

    private String invoice_company_address;

    private String invoice_company_zip;

    private String invoice_company_vat_number;

    private String invoice_company_iban;

    private String invoice_company_registration_number;

    private String invoice_tax_number;

    private String invoice_status;

    @ManyToOne
    @JoinColumn(name = "\"company\"")
    private Company company;

    private String pdf_name;

  }
