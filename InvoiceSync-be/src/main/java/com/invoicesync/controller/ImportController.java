package com.invoicesync.controller;


import com.invoicesync.module.Import;
import com.invoicesync.module.InvoiceImport;
import com.invoicesync.ocr.service.OCRService;
import com.invoicesync.service.ImportService;
import com.invoicesync.service.InvoiceImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasRole('USER')")
public class ImportController {

    private final ImportService importService;
    private final JdbcTemplate jdbcTemplate;
    private final OCRService ocrService;

    @Autowired
    private InvoiceImportService invoiceImportService;

    @Autowired
    public ImportController(final ImportService importService, final JdbcTemplate jdbcTemplate,
                            final OCRService ocrService, final InvoiceImportService invoiceImportService) {
        this.importService = importService;
        this.jdbcTemplate = jdbcTemplate;
        this.ocrService = ocrService;
        this.invoiceImportService = invoiceImportService;
    }

    //get all imports
    @GetMapping("/import")
    @PreAuthorize("hasAuthority('user:read')")
    public List<Import> getImports() {
        return importService.getImports();
    }

    //get import by id
    @GetMapping("/import/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public Import getImportById(@PathVariable final int id) {
        return importService.getImport(id);
    }

    //get import by user id
    @GetMapping("/import/user/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public List<Map<String, String>> getImportsByUserId(@PathVariable final int id) {
        final List<Import> imports = importService.getImportsByUserId(id);
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
            final String ocrText = ocrService.extractTextFromPDF(pdfPath);
            final String dueDateStr = ocrService.findDueDate(ocrText);
            final String issueDateStr = ocrService.findIssueDate(ocrText);

            invoice.setInvoiceDueDate(dueDateStr);
            invoice.setInvoiceIssueDate(issueDateStr);
            invoice.setInvoiceStatus("UNPROCESSED");

            invoiceImportService.saveInvoice(invoice);
            return "Invoice successfully imported!";
        } catch (IOException e) {
            return "Error occurred: " + e.getMessage();
        }
    }
}
