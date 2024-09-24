package com.invoicesync.controller;


import com.invoicesync.module.Import;
import com.invoicesync.module.InvoiceImport;
import com.invoicesync.ocr.service.OCRService;
import com.invoicesync.service.ImportService;
import com.invoicesync.service.InvoiceImportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import autovalue.shaded.kotlinx.metadata.internal.protobuf.ByteString;

@RestController
@RequestMapping("/api/v1")
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
    public List<Import> getImports() {
        return importService.getImports();
    }

    //get import by id
    @GetMapping("/import/{id}")
    public Import getImportById(@PathVariable int id) {
        return importService.getImport(id);
    }

    //get import by user id
    @GetMapping("/import/user/{id}")
    public List<Map<String, String>> getImportsByUserId(@PathVariable int id) {
        List<Import> imports = importService.getImportsByUserId(id);
        List<Map<String, String>> result = new ArrayList<>();
        for (Import anImport : imports) {
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(anImport.getId()));
            map.put("company_id", String.valueOf(anImport.getCompany().getId()));
            map.put("filename", anImport.getFilename());
            map.put("username", anImport.getUser().getUsername());
            map.put("created_at", anImport.getCreatedAt());
            map.put("company_name" , anImport.getCompany().getName());
            result.add(map);
        }
        return result;
    }

    //get import by company id for user
    @GetMapping("/import/user/{userId}/company/{companyId}")
    public List<Map<String,String>> getImportsByCompanyId(@PathVariable int userId, @PathVariable int companyId ) {
        List<Import> imports = importService.getImportsByCompanyId(userId, companyId);
        List<Map<String, String>> result = new ArrayList<>();
        for (Import anImport : imports) {
            Map<String, String> map = new HashMap<>();
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
    public String getXmlContentById (@PathVariable int import_id){
        Import xml_content = importService.getXmlContentById(import_id);
        return xml_content.getXmlContent();
    }

    //get script name and schema name from database by import id
    @GetMapping("/execute-script/import/{import_id}")
    public ResponseEntity<String> executeScript(@PathVariable int import_id) {
        // Získanie company_id a credential_id z import_file tabuľky
        String importSql = "SELECT company_id, credential_id FROM invoice_sync.import_file WHERE id = ?";
        Map<String, Object> importResult = jdbcTemplate.queryForMap(importSql, import_id);
        int companyId = (int) importResult.get("company_id");
        int credentialId = (int) importResult.get("credential_id");

        // Získanie skriptu z script_schema_mapping tabuľky
        String scriptSql = "SELECT script_name, schema_name FROM invoice_sync.script_schema_mapping WHERE company_id = ? AND credential_id = ?";
        Map<String, Object> scriptResult = jdbcTemplate.queryForMap(scriptSql, companyId, credentialId);
        String scriptName = (String) scriptResult.get("script_name");
        String schemaName = (String) scriptResult.get("schema_name");

        // Logovanie získaných hodnôt
        System.out.println("Company ID: " + companyId);
        System.out.println("Credential ID: " + credentialId);
        System.out.println("Script Name: " + scriptName);
        System.out.println("Schema Name: " + schemaName);

        return ResponseEntity.ok("Company ID: " + companyId + "\nCredential ID: " + credentialId + "\nScript Name: " + scriptName + "\nSchema Name: " + schemaName);
    }

    @GetMapping("/extract-text")
    public String extractText(@RequestParam String pdfPath) {
        try {
            String ocrText = ocrService.extractTextFromPDF(pdfPath);
            String dateDue = ocrService.findDueDate(ocrText);
            String dateIssue = ocrService.findIssueDate(ocrText);
            String deliveryDate = ocrService.findDeliveryDate(ocrText);
            String variableSymbol = ocrService.findVariableSymbol(ocrText);
            String vatId = ocrService.findVatId(ocrText);
            String iban = ocrService.findIban(ocrText);
            String ico = ocrService.findIco(ocrText);
            String dic = ocrService.findDic(ocrText);
            return iban + "\n" +ico + "\n" + dic;
        } catch (IOException e) {
            return "Error occurred: " + e.getMessage();
        }
    }

  @PostMapping("/import-invoice")
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
