package com.invoicesync.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoicesync.dto.InvoiceImportResponseDto;
import com.invoicesync.module.InvoiceImport;
import com.invoicesync.repository.InvoiceImportRepository;

@Service
public class InvoiceImportServiceImpl implements InvoiceImportService {

  private final InvoiceImportRepository invoiceImportRepository;

  @Autowired
  public InvoiceImportServiceImpl(final InvoiceImportRepository invoiceImportRepository) {
    this.invoiceImportRepository = invoiceImportRepository;
  }

  @Override
  public List<InvoiceImportResponseDto> getInoivceImportsByUserId(Long userId) {
    return invoiceImportRepository.findByUserId(userId)
        .stream()
        .map(invoiceImport -> new InvoiceImportResponseDto(
            invoiceImport.getId(),
            invoiceImport.getInvoice_number(),
            invoiceImport.getInvoice_import_date(),
            invoiceImport.getInvoice_delivery_date(),
            invoiceImport.getInvoice_issue_date(),
            invoiceImport.getInvoice_due_date(),
            invoiceImport.getInvoice_variable_symbol(),
            invoiceImport.getInvoice_total_amount(),
            invoiceImport.getInvoice_company_name(),
            invoiceImport.getInvoice_company_city(),
            invoiceImport.getInvoice_company_address(),
            invoiceImport.getInvoice_company_zip(),
            invoiceImport.getInvoice_company_vat_number(),
            invoiceImport.getInvoice_company_iban(),
            invoiceImport.getInvoice_company_registration_number(),
            invoiceImport.getInvoice_tax_number(),
            invoiceImport.getInvoice_status(),
            invoiceImport.getCompany().getId()
        ))
        .collect(Collectors.toList());
  }

  public InvoiceImport saveInvoice(InvoiceImport invoice) {
    return invoiceImportRepository.save(invoice);
  }

  @Override
  public InvoiceImportResponseDto getInvoiceById(Long id) {
    return invoiceImportRepository.findById(id)
        .map(invoiceImport -> new InvoiceImportResponseDto(
            invoiceImport.getId(),
            invoiceImport.getInvoice_number(),
            invoiceImport.getInvoice_import_date(),
            invoiceImport.getInvoice_delivery_date(),
            invoiceImport.getInvoice_issue_date(),
            invoiceImport.getInvoice_due_date(),
            invoiceImport.getInvoice_variable_symbol(),
            invoiceImport.getInvoice_total_amount(),
            invoiceImport.getInvoice_company_name(),
            invoiceImport.getInvoice_company_city(),
            invoiceImport.getInvoice_company_address(),
            invoiceImport.getInvoice_company_zip(),
            invoiceImport.getInvoice_company_vat_number(),
            invoiceImport.getInvoice_company_iban(),
            invoiceImport.getInvoice_company_registration_number(),
            invoiceImport.getInvoice_tax_number(),
            invoiceImport.getInvoice_status(),
            invoiceImport.getCompany().getId()
        ))
        .orElseThrow(() -> new IllegalArgumentException("Invoice with id " + id + " not found"));
  }

  @Override
  public List<InvoiceImportResponseDto> getInvoiceImportsByCompanyIdCurrentUser(final Long companyId,
      final Long currentUserId) {
    return invoiceImportRepository.findByCompanyIdAndUserId(companyId, currentUserId)
        .stream()
        .map(invoiceImport -> new InvoiceImportResponseDto(
            invoiceImport.getId(),
            invoiceImport.getInvoice_number(),
            invoiceImport.getInvoice_import_date(),
            invoiceImport.getInvoice_delivery_date(),
            invoiceImport.getInvoice_issue_date(),
            invoiceImport.getInvoice_due_date(),
            invoiceImport.getInvoice_variable_symbol(),
            invoiceImport.getInvoice_total_amount(),
            invoiceImport.getInvoice_company_name(),
            invoiceImport.getInvoice_company_city(),
            invoiceImport.getInvoice_company_address(),
            invoiceImport.getInvoice_company_zip(),
            invoiceImport.getInvoice_company_vat_number(),
            invoiceImport.getInvoice_company_iban(),
            invoiceImport.getInvoice_company_registration_number(),
            invoiceImport.getInvoice_tax_number(),
            invoiceImport.getInvoice_status(),
            invoiceImport.getCompany().getId()
        ))
        .collect(Collectors.toList());
  }

}
