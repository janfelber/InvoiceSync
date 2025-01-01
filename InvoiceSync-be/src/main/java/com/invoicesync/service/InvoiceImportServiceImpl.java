package com.invoicesync.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.dto.InvoiceImportResponseDto;
import com.invoicesync.module.InvoiceImport;
import com.invoicesync.ocr.service.OCRService;
import com.invoicesync.repository.InvoiceImportRepository;
import com.invoicesync.user.UserDemo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceImportServiceImpl implements InvoiceImportService {

  private final InvoiceImportRepository invoiceImportRepository;

  private final OCRService ocrService;

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

  public InvoiceImport saveInvoice(final MultipartFile file) throws IOException {
    final InvoiceImport invoiceImport = new InvoiceImport();
    final String ocrText = ocrService.extractTextFromPDF(file);
    final String pdfName = file.getOriginalFilename();
    final Long userId = ((UserDemo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

    final String uploadDir = "src/uploads/users/" + userId;
    final Path targetPath = Path.of(uploadDir, pdfName);

    Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

    invoiceImport.setUser((UserDemo) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    final String vatId = ocrService.findVatId(ocrText);
    final String iban = ocrService.findIban(ocrText);
    final String ico = ocrService.findIco(ocrText);
    final String dic = ocrService.findDic(ocrText);
    final String dueDateStr = ocrService.findDueDate(ocrText);
    final String issueDateStr = ocrService.findIssueDate(ocrText);
    final String deliveryDateStr = ocrService.findDeliveryDate(ocrText);
    final String variableSymbol = ocrService.findVariableSymbol(ocrText);

    final String supplierSection = ocrService.extractSupplierSection(ocrText);
    final String supplierName = ocrService.extractSupplierName(supplierSection);
    final String supplierAddress = ocrService.extractSupplierAddress(supplierSection);
    final String supplierPostalCode = ocrService.extractSupplierPostalCode(supplierSection);
    final String supplierCity = ocrService.extractSupplierCity(supplierSection);
    invoiceImport.setInvoice_company_vat_number(vatId);
    invoiceImport.setInvoice_company_iban(iban);
    invoiceImport.setInvoice_company_registration_number(ico);
    invoiceImport.setInvoice_tax_number(dic);
    invoiceImport.setInvoice_import_date(new java.util.Date());
    invoiceImport.setInvoice_issue_date(issueDateStr);
    invoiceImport.setInvoice_delivery_date(deliveryDateStr);
    invoiceImport.setInvoice_due_date(dueDateStr);
    invoiceImport.setInvoice_variable_symbol(variableSymbol);
    invoiceImport.setInvoice_company_name(supplierName);
    invoiceImport.setInvoice_company_zip(supplierPostalCode);
    invoiceImport.setInvoice_company_address(supplierAddress);
    invoiceImport.setInvoice_company_city(supplierCity);
    invoiceImport.setInvoice_status("UNPROCESSED");
    invoiceImport.setPdf_name(pdfName);

    return invoiceImportRepository.save(invoiceImport);
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
