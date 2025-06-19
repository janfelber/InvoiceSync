package com.invoicesync.invoice;

  import java.util.Date;

  import com.invoicesync.company.Company;

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

    // @ManyToOne
    // @JoinColumn(name = "\"user_id\"")
    // private UserDemo user;

    private String invoice_number;

    private Date import_date;

    private String issue_date;

    private String tax_date;

    private String accounting_date;

    private String due_date;

    private String variable_symbol;

    private String partner_name;

    private String partner_city;

    private String partner_street;

    private String partner_zip;

    private String partner_vat_id;

    private String partner_registration_number;

    private String partner_tax_id;

    private String status;

    @ManyToOne
    @JoinColumn(name = "\"company\"")
    private Company company;

    private String pdf_name;

  }
