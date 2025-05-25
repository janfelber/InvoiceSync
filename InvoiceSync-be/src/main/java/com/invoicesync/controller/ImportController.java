// package com.invoicesync.controller;
//
// import java.io.IOException;
// import java.util.List;
//
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;
//
// import com.invoicesync.dto.invoice.response.InvoiceImportResponseDto;
// import com.invoicesync.ocr.service.OCRService;
// import com.invoicesync.service.ImportService;
// import com.invoicesync.service.InvoiceImportService;
// import com.invoicesync.service.XmlFileService;
// import com.invoicesync.user.CurrentUserService;
//
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequestMapping("/api/v1")
// @RequiredArgsConstructor
// @PreAuthorize("hasRole('USER')")
// public class ImportController {
//
//     private final ImportService importService;
//     private final JdbcTemplate jdbcTemplate;
//     private final OCRService ocrService;
//     private final XmlFileService xmlFileService;
//     private final CurrentUserService currentUserService;
//     private final InvoiceImportService invoiceImportService;
//
//
//     //get all imports
//     @GetMapping("/import")
//     @PreAuthorize("hasAuthority('user:read')")
//     public List<Import> getImports() {
//         return importService.getImports();
//     }
//
//     //get import by id
//     @GetMapping("/import/{id}")
//     @PreAuthorize("hasAuthority('user:read')")
//     public InvoiceImportResponseDto getImportById(@PathVariable final Long id) {
//         return invoiceImportService.getInvoiceById(id);
//     }
//
//     //get import by user id
//     @GetMapping("/import/user")
//     @PreAuthorize("hasAuthority('user:read')")
//     public List<InvoiceImportResponseDto> getImportsByUserId() {
//         final Long userId = currentUserService.getCurrentUserId();
//         return invoiceImportService.getInoivceImportsByUserId(userId);
//     }
//
//     // get content of xml file by import id
//     @GetMapping("/import/xml/{import_id}")
//     @PreAuthorize("hasAuthority('user:read')")
//     public String getXmlContentById(@PathVariable final int import_id) {
//         final Import xml_content = importService.getXmlContentById(import_id);
//         return xml_content.getXmlContent();
//     }
//
//     /**
//      * Retrieves a list of invoice imports associated with the specified company ID and the current authenticated user.
//      *
//      * @param companyId the ID of the company for which to retrieve invoice imports
//      * @return a list of {@link InvoiceImportResponseDto} representing the invoice imports
//      */
//     @GetMapping("/imports/company/{companyId}/current-user")
//     @PreAuthorize("hasAuthority('user:read')")
//     public List<InvoiceImportResponseDto> getImportsByCompanyIdCurrentUser(@PathVariable final Long companyId) {
//         final Long currentUserId = currentUserService.getCurrentUserId();
//         return invoiceImportService.getInvoiceImportsByCompanyIdCurrentUser(companyId, currentUserId);
//     }
//
//     @GetMapping("/extract-text")
//     @PreAuthorize("hasAuthority('user:read')")
//     public String extractText(@RequestParam final MultipartFile pdfPath) {
//         try {
//             final String ocrText = ocrService.extractTextFromPDF(pdfPath);
//             final String dateDue = ocrService.findDueDate(ocrText);
//             final String dateIssue = ocrService.findIssueDate(ocrText);
//             final String deliveryDate = ocrService.findDeliveryDate(ocrText);
//             final String variableSymbol = ocrService.findVariableSymbol(ocrText);
//             final String vatId = ocrService.findVatId(ocrText);
//             final String iban = ocrService.findIban(ocrText);
//             final String ico = ocrService.findIco(ocrText);
//             final String dic = ocrService.findDic(ocrText);
//             return iban + "\n" + ico + "\n" + dic;
//         } catch (IOException e) {
//             return "Error occurred: " + e.getMessage();
//         }
//     }
//
//     @PostMapping("/import-invoice")
//     @PreAuthorize("hasAuthority('user:create')")
//     public ResponseEntity<List<String>> importInvoice(@RequestParam("file") final MultipartFile pdfFile,
//         final Long companyId) {
//         try {
//             invoiceImportService.saveInvoice(pdfFile, companyId);
//             return ResponseEntity.status(HttpStatus.CREATED).build();
//         } catch (IOException e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }
// }
