package com.invoicesync.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceImportResponseDto {

  private Long id;

  private String invoice_number;

  private Date invoice_import_date;

  private String invoice_delivery_date;

  private String invoice_issue_date;

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

  private Long company;

}
