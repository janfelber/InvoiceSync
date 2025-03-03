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

import com.invoicesync.dto.identity.PartnerDTO;
import com.invoicesync.dto.invoice.pohoda.InvoiceResponseDetailsDTO;
import com.invoicesync.dto.invoice.response.InvoiceImportResponseDto;
import com.invoicesync.module.Company;
import com.invoicesync.module.InvoiceImport;
import com.invoicesync.ocr.service.OCRService;
import com.invoicesync.repository.CompanyRepository;
import com.invoicesync.repository.InvoiceImportRepository;
import com.invoicesync.user.UserDemo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceImportServiceImpl implements InvoiceImportService {

  private final InvoiceImportRepository invoiceImportRepository;

  private final CompanyRepository companyRepository;

  private final OCRService ocrService;

  @Override
  public List<InvoiceImportResponseDto> getInoivceImportsByUserId(Long userId) {
    return invoiceImportRepository.findByUserId(userId)
        .stream()
        .map(invoiceImport -> new InvoiceImportResponseDto(
            invoiceImport.getId(),
            invoiceImport.getImport_date(),
            new InvoiceResponseDetailsDTO(
                invoiceImport.getInvoice_number(),
                invoiceImport.getVariable_symbol(),
                invoiceImport.getVariable_symbol(),
                invoiceImport.getIssue_date(),
                invoiceImport.getTax_date(),
                invoiceImport.getDue_date()
            ),
            new PartnerDTO(
                invoiceImport.getPartner_name(),
                invoiceImport.getPartner_city(),
                invoiceImport.getPartner_street(),
                invoiceImport.getPartner_zip(),
                invoiceImport.getPartner_registration_number(),
                invoiceImport.getPartner_tax_id(),
                invoiceImport.getPartner_vat_id()
            ),
            invoiceImport.getStatus(),
            invoiceImport.getCompany().getId()
        ))
        .collect(Collectors.toList());
  }

  public InvoiceImport saveInvoice(final MultipartFile file, final Long companyId) throws IOException {
    final InvoiceImport invoiceImport = new InvoiceImport();
    final String ocrText = ocrService.extractTextFromPDF(file);
    final String pdfName = file.getOriginalFilename();
    final Long userId = ((UserDemo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

    final String uploadDir = "src/uploads/users/" + userId;
    final Path targetPath = Path.of(uploadDir, pdfName);

    Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

    final Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new RuntimeException("Company not found"));

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
    invoiceImport.setPartner_vat_id(vatId);
    invoiceImport.setPartner_tax_id(dic);
    invoiceImport.setPartner_registration_number(ico);
    invoiceImport.setImport_date(new java.util.Date());
    invoiceImport.setIssue_date(issueDateStr);
    invoiceImport.setDue_date(dueDateStr);
    invoiceImport.setTax_date(deliveryDateStr);
    invoiceImport.setVariable_symbol(variableSymbol);
    invoiceImport.setPartner_name(supplierName);
    invoiceImport.setPartner_zip(supplierPostalCode);
    invoiceImport.setPartner_street(supplierAddress);
    invoiceImport.setPartner_city(supplierCity);
    invoiceImport.setStatus("UNPROCESSED");
    invoiceImport.setPdf_name(pdfName);
    invoiceImport.setCompany(company);

    return invoiceImportRepository.save(invoiceImport);
  }

  @Override
  public InvoiceImportResponseDto getInvoiceById(Long id) {
    return invoiceImportRepository.findById(id)
        .map(invoiceImport -> new InvoiceImportResponseDto(
            invoiceImport.getId(),
            invoiceImport.getImport_date(),
            new InvoiceResponseDetailsDTO(
                invoiceImport.getInvoice_number(),
                invoiceImport.getVariable_symbol(),
                invoiceImport.getVariable_symbol(),
                invoiceImport.getIssue_date(),
                invoiceImport.getTax_date(),
                invoiceImport.getDue_date()
            ),
            new PartnerDTO(
                invoiceImport.getPartner_name(),
                invoiceImport.getPartner_city(),
                invoiceImport.getPartner_street(),
                invoiceImport.getPartner_zip(),
                invoiceImport.getPartner_registration_number(),
                invoiceImport.getPartner_tax_id(),
                invoiceImport.getPartner_vat_id()
            ),
            invoiceImport.getStatus(),
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
            invoiceImport.getImport_date(),
            new InvoiceResponseDetailsDTO(
                invoiceImport.getInvoice_number(),
                invoiceImport.getVariable_symbol(),
                invoiceImport.getVariable_symbol(),
                invoiceImport.getIssue_date(),
                invoiceImport.getTax_date(),
                invoiceImport.getDue_date()
            ),
            new PartnerDTO(
                invoiceImport.getPartner_name(),
                invoiceImport.getPartner_city(),
                invoiceImport.getPartner_street(),
                invoiceImport.getPartner_zip(),
                invoiceImport.getPartner_registration_number(),
                invoiceImport.getPartner_tax_id(),
                invoiceImport.getPartner_vat_id()
            ),
            invoiceImport.getStatus(),
            invoiceImport.getCompany().getId()
        ))
        .collect(Collectors.toList());
  }

}
