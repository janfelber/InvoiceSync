package com.invoicesync.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.dto.InvoiceImportResponseDto;
import com.invoicesync.module.Import;
import com.invoicesync.module.InvoiceImport;
import com.invoicesync.ocr.service.OCRService;
import com.invoicesync.service.ImportService;
import com.invoicesync.service.InvoiceImportService;
import com.invoicesync.service.XmlFileService;
import com.invoicesync.user.CurrentUserService;
import com.invoicesync.user.UserDemo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ImportController {

    private final ImportService importService;
    private final JdbcTemplate jdbcTemplate;
    private final OCRService ocrService;
    private final XmlFileService xmlFileService;
    private final CurrentUserService currentUserService;
    private final InvoiceImportService invoiceImportService;


    //get all imports
    @GetMapping("/import")
    @PreAuthorize("hasAuthority('user:read')")
    public List<Import> getImports() {
        return importService.getImports();
    }

    //get import by id
    @GetMapping("/import/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public InvoiceImportResponseDto getImportById(@PathVariable final Long id) {
        return invoiceImportService.getInvoiceById(id);
    }

    //get import by user id
    @GetMapping("/import/user")
    @PreAuthorize("hasAuthority('user:read')")
    public List<InvoiceImportResponseDto> getImportsByUserId() {
        final Long userId = currentUserService.getCurrentUserId();
        return invoiceImportService.getInoivceImportsByUserId(userId);
    }


    //get import by company id for user
    @GetMapping("/import/user/{userId}/company/{companyId}")
    @PreAuthorize("hasAuthority('user:read')")
    public List<Map<String, String>> getImportsByCompanyId(@PathVariable final int userId, @PathVariable final int companyId) {
        final List<Import> imports = importService.getImportsByCompanyId(userId, companyId);
        final List<Map<String, String>> result = new ArrayList<>();
        for (final Import anImport : imports) {
            final Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(anImport.getId()));
            map.put("company_id", String.valueOf(anImport.getCompany().getId()));
            map.put("filename", anImport.getFilename());
            map.put("username", anImport.getUser().getUsername());
            map.put("created_at", anImport.getCreatedAt());
            map.put("company_name", anImport.getCompany().getName());
            result.add(map);
        }
        return result;
    }

    // get content of xml file by import id
    @GetMapping("/import/xml/{import_id}")
    @PreAuthorize("hasAuthority('user:read')")
    public String getXmlContentById(@PathVariable final int import_id) {
        final Import xml_content = importService.getXmlContentById(import_id);
        return xml_content.getXmlContent();
    }

    //get script name and schema name from database by import id
    @GetMapping("/execute-script/import/{import_id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<String> executeScript(@PathVariable final int import_id) {
        // Získanie company_id a credential_id z import_file tabuľky
        final String importSql = "SELECT company_id, credential_id FROM invoice_sync.import_file WHERE id = ?";
        final Map<String, Object> importResult = jdbcTemplate.queryForMap(importSql, import_id);
        final int companyId = (int) importResult.get("company_id");
        final int credentialId = (int) importResult.get("credential_id");

        // Získanie skriptu z script_schema_mapping tabuľky
        final String scriptSql = "SELECT script_name, schema_name FROM invoice_sync.script_schema_mapping WHERE company_id = ? AND credential_id = ?";
        final Map<String, Object> scriptResult = jdbcTemplate.queryForMap(scriptSql, companyId, credentialId);
        final String scriptName = (String) scriptResult.get("script_name");
        final String schemaName = (String) scriptResult.get("schema_name");

        // Logovanie získaných hodnôt
        System.out.println("Company ID: " + companyId);
        System.out.println("Credential ID: " + credentialId);
        System.out.println("Script Name: " + scriptName);
        System.out.println("Schema Name: " + schemaName);

        return ResponseEntity.ok("Company ID: " + companyId + "\nCredential ID: " + credentialId + "\nScript Name: " + scriptName + "\nSchema Name: " + schemaName);
    }

    @GetMapping("/extract-text")
    @PreAuthorize("hasAuthority('user:read')")
    public String extractText(@RequestParam final String pdfPath) {
        try {
            final String ocrText = ocrService.extractTextFromPDF(pdfPath);
            final String dateDue = ocrService.findDueDate(ocrText);
            final String dateIssue = ocrService.findIssueDate(ocrText);
            final String deliveryDate = ocrService.findDeliveryDate(ocrText);
            final String variableSymbol = ocrService.findVariableSymbol(ocrText);
            final String vatId = ocrService.findVatId(ocrText);
            final String iban = ocrService.findIban(ocrText);
            final String ico = ocrService.findIco(ocrText);
            final String dic = ocrService.findDic(ocrText);
            return iban + "\n" + ico + "\n" + dic;
        } catch (IOException e) {
            return "Error occurred: " + e.getMessage();
        }
    }

    @PostMapping("/import-invoice")
    @PreAuthorize("hasAuthority('user:create')")
    public String importInvoice(@RequestParam final String pdfPath) {
        final InvoiceImport invoice = new InvoiceImport();
        try {
            Long currentUserId = currentUserService.getCurrentUserId();
            final String ocrText = ocrService.extractTextFromPDF(pdfPath);
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

            UserDemo hardcodedUser = new UserDemo();
            hardcodedUser.setId(34L);
            invoice.setUser(hardcodedUser);
            invoice.setInvoice_company_vat_number(vatId);
            invoice.setInvoice_company_iban(iban);
            invoice.setInvoice_company_registration_number(ico);
            invoice.setInvoice_tax_number(dic);
            invoice.setInvoice_import_date(new java.util.Date());
            invoice.setInvoice_issue_date(issueDateStr);
            invoice.setInvoice_delivery_date(deliveryDateStr);
            invoice.setInvoice_due_date(dueDateStr);
            invoice.setInvoice_variable_symbol(variableSymbol);
            invoice.setInvoice_company_name(supplierName);
            invoice.setInvoice_company_zip(supplierPostalCode);
            invoice.setInvoice_company_address(supplierAddress);
            invoice.setInvoice_company_city(supplierCity);
            invoice.setInvoice_status("UNPROCESSED");

            invoiceImportService.saveInvoice(invoice);
            return "Invoice successfully imported!";
        } catch (IOException e) {
            return "Error occurred: " + e.getMessage();
        }
    }
}
