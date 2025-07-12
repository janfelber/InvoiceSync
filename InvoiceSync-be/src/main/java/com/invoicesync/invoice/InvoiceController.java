package com.invoicesync.invoice;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoicesync.invoice.dto.InvoiceImportResponseDto;
import com.invoicesync.invoice.dto.InvoiceResponseDTO;
import com.invoicesync.ocr.OCRServiceImpl;
import com.invoicesync.shared.common.PageResponse;
import com.invoicesync.subscription.UserService;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

  private final OCRServiceImpl ocrService;

  private final InvoiceService invoiceService;

  private final ObjectMapper objectMapper;

  private final InvoiceMapper invoiceMapper;

  private final InvoiceRepository invoiceRepository;

  @Qualifier("openAiWebClient")
  private final WebClient openAiClient;

  @Value("${openai.api.key}")
  private String openAiApiKey;

  @Autowired
  public InvoiceController(
      final OCRServiceImpl ocrService,
      final InvoiceService invoiceService, final UserService userService, final ObjectMapper objectMapper,
      final InvoiceMapper invoiceMapper, final InvoiceRepository invoiceRepository,
      @Qualifier("openAiWebClient") final WebClient openAiClient
  ) {
    this.ocrService = ocrService;
    this.invoiceService = invoiceService;
    this.objectMapper = objectMapper;
    this.invoiceMapper = invoiceMapper;
    this.invoiceRepository = invoiceRepository;
    this.openAiClient = openAiClient;
  }

  // //get all imports
  // @GetMapping("/import")
  // @PreAuthorize("hasAuthority('user:read')")
  // public List<Import> getImports() {
  //   return importService.getImports();
  // }

  @GetMapping("/company/{company-id}")
  public ResponseEntity<PageResponse<InvoiceResponseDTO>> findInvoicesByCompanyId(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      @PathVariable("company-id") final Long companyId,
      final Authentication connectedUser) {
    return ResponseEntity.ok(
        invoiceService.findInvoicesByCompanyId(page, size, companyId, connectedUser));
  }

  @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Long> extractFromScannedInvoice(@RequestParam("file") final MultipartFile pdfFile,
      final Authentication connectedUser) throws IOException {
    // 1. OCR
    final String pdfText = ocrService.extractTextFromPDF(pdfFile, connectedUser);

    System.out.println("PDF Text: " + pdfText);

    // 2. Prompt
    final String prompt = """
        Extract the following fields from the invoice text and return them in JSON format:
        - Invoice Number
        - Date of Issue
        - Date of Delivery
        - Date of Due
        - Variable Symbol
        - Supplier VAT ID
        - Invoice Items (description, quantity, unit price, total)
        
        Return only the raw JSON without any explanation or extra text.
        
        Invoice text:
        """ + pdfText;

    // 3. API request
    final Map<String, Object> requestBody = Map.of(
        "model", "gpt-4o-mini",
        "messages", List.of(Map.of("role", "user", "content", prompt))
    );

    // final String gptResponse = openAiClient.post()
    //     .uri("/chat/completions")
    //     .header("Authorization", "Bearer " + openAiApiKey)
    //     .header("Content-Type", "application/json")
    //     .bodyValue(requestBody)
    //     .retrieve()
    //     .bodyToMono(OpenAiResponse.class)
    //     .map(r -> r.getChoices().get(0).getMessage().getContent())
    //     .block();
    //
    // // 4. Očisti JSON
    // final String cleanedJson = gptResponse
    //     .replaceAll("^```json\\s*", "")
    //     .replaceAll("```$", "")
    //     .trim();
    //
    // System.out.println("Cleaned JSON: " + cleanedJson);

    final String json = """
        {
          "Invoice Number": "10/2023/602",
          "Date of Issue": "15.2.2023",
          "Date of Delivery": "15.2.2023",
          "Date of Due": "1.4.2023",
          "Variable Symbol": "231000602",
          "Supplier VAT ID": "SK2024181346",
          "Invoice Items": [
            {
              "description": "Filtr 3M 6059, 1 pár=2ks",
              "quantity": 15,
              "unit price": 10.102,
              "total": 151.52
            },
            {
              "description": "Štít ŠP 29",
              "quantity": 10,
              "unit price": 12.20,
              "total": 122.00
            }
          ]
        }
        """;

    // 5. Parse JSON
    final InvoiceRequest dto = objectMapper.readValue(json, InvoiceRequest.class);

    // 6. Ulož do DB
    final Invoice invoice = invoiceMapper.toInvoice(dto);
    invoiceRepository.save(invoice);

    return ResponseEntity.ok(invoice.getId());
  }

  public static class OpenAiResponse {

    private java.util.List<Choice> choices;

    public java.util.List<Choice> getChoices() {
      return choices;
    }

    public void setChoices(final java.util.List<Choice> choices) {
      this.choices = choices;
    }

    public static class Choice {

      private Message message;

      public Message getMessage() {
        return message;
      }

      public void setMessage(final Message message) {
        this.message = message;
      }

    }

    public static class Message {

      private String role;

      private String content;

      public String getRole() {
        return role;
      }

      public void setRole(final String role) {
        this.role = role;
      }

      public String getContent() {
        return content;
      }

      public void setContent(final String content) {
        this.content = content;
      }

    }

  }

  // //get import by id
  // @GetMapping("/import/{id}")
  // @PreAuthorize("hasAuthority('user:read')")
  // public InvoiceImportResponseDto getImportById(@PathVariable final Long id) {
  //   return invoiceImportService.getInvoiceById(id);
  // }

  //get import by user id
  // @GetMapping("/import/user")
  // @PreAuthorize("hasAuthority('user:read')")
  // public List<InvoiceImportResponseDto> getImportsByUserId() {
  //   final Long userId = currentUserService.getCurrentUserId();
  //   return invoiceImportService.getInoivceImportsByUserId(userId);
  // }

  // get content of xml file by import id
  // @GetMapping("/import/xml/{import_id}")
  // @PreAuthorize("hasAuthority('user:read')")
  // public String getXmlContentById(@PathVariable final int import_id) {
  //   final Import xml_content = importService.getXmlContentById(import_id);
  //   return xml_content.getXmlContent();
  // }

  /**
   * Retrieves a list of invoice imports associated with the specified company ID and the current authenticated user.
   *
   * @param companyId the ID of the company for which to retrieve invoice imports
   * @return a list of {@link InvoiceImportResponseDto} representing the invoice imports
   */
  // @GetMapping("/imports/company/{companyId}/current-user")
  // @PreAuthorize("hasAuthority('user:read')")
  // public List<InvoiceImportResponseDto> getImportsByCompanyIdCurrentUser(@PathVariable final Long companyId) {
  //   final Long currentUserId = currentUserService.getCurrentUserId();
  //   return invoiceImportService.getInvoiceImportsByCompanyIdCurrentUser(companyId, currentUserId);
  // }
  // @GetMapping("/extract-text")
  // @PreAuthorize("hasAuthority('user:read')")
  // public String extractText(@RequestParam final MultipartFile pdfPath) {
  //   try {
  //     final String ocrText = ocrService.extractTextFromPDF(pdfPath);
  //     final String dateDue = ocrService.findDueDate(ocrText);
  //     final String dateIssue = ocrService.findIssueDate(ocrText);
  //     final String deliveryDate = ocrService.findDeliveryDate(ocrText);
  //     final String variableSymbol = ocrService.findVariableSymbol(ocrText);
  //     final String vatId = ocrService.findVatId(ocrText);
  //     final String iban = ocrService.findIban(ocrText);
  //     final String ico = ocrService.findIco(ocrText);
  //     final String dic = ocrService.findDic(ocrText);
  //     return iban + "\n" + ico + "\n" + dic;
  //   } catch (IOException e) {
  //     return "Error occurred: " + e.getMessage();
  //   }
  // }

  // @PostMapping("/import-invoice")
  // @PreAuthorize("hasAuthority('user:create')")
  // public ResponseEntity<List<String>> importInvoice(@RequestParam("file") final MultipartFile pdfFile,
  //     final Long companyId) {
  //   try {
  //     invoiceImportService.saveInvoice(pdfFile, companyId);
  //     return ResponseEntity.status(HttpStatus.CREATED).build();
  //   } catch (IOException e) {
  //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  //   }
  // }

}
