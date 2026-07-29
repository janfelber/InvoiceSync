package com.invoicesync.modules.invoice.service;

import static com.invoicesync.modules.invoice.InvoiceSpecification.withCompanyId;
import static com.invoicesync.modules.invoice.InvoiceSpecification.withUserId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.common.PageableFactory;
import com.invoicesync.core.enums.LimitType;
import com.invoicesync.core.exception.DownloadDocumentException;
import com.invoicesync.core.filestorage.service.FileStorageService;
import com.invoicesync.modules.activity.model.UserActivityType;
import com.invoicesync.modules.activity.service.UserActivityRecord;
import com.invoicesync.modules.activity.service.UserActivityService;
import com.invoicesync.modules.auth.security.OwnedCompany;
import com.invoicesync.modules.auth.security.OwnedInvoice;
import com.invoicesync.modules.auth.security.OwnedInvoiceDocument;
import com.invoicesync.modules.auth.security.RequiresOwnership;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.company.repository.CompanyRepository;
import com.invoicesync.modules.document.model.AddDocumentData;
import com.invoicesync.modules.document.model.DocumentTableResponse;
import com.invoicesync.modules.document.model.InvoiceDocument;
import com.invoicesync.modules.document.repository.InvoiceDocumentRepository;
import com.invoicesync.modules.invoice.mapper.InvoiceMapper;
import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.invoice.model.InvoiceCreate;
import com.invoicesync.modules.invoice.model.InvoiceRequest;
import com.invoicesync.modules.invoice.model.InvoiceResponse;
import com.invoicesync.modules.invoice.model.InvoiceResponseTable;
import com.invoicesync.modules.invoice.repository.InvoiceRepository;
import com.invoicesync.modules.ocr.OCRService;
import com.invoicesync.modules.subscription.guard.service.LimitGuardService;
import com.invoicesync.partner.CompaniesRegistry;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;

@Service
public class InvoiceServiceImpl implements InvoiceService {

  private static final Pattern ODBERATEL_BLOCK =
      Pattern.compile("(?s)Odberateľ:?(.+?)(Faktúra č\\.|Dátum vystavenia|Faktúra)", Pattern.CASE_INSENSITIVE);

  private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

  final CompaniesRegistry subjectRegistry;

  private final InvoiceRepository invoiceRepository;

  private final InvoiceDocumentRepository invoiceDocumentRepository;

  private final LimitGuardService limitGuardService;

  private final CompanyRepository companyRepository;

  private final OCRService ocrService;

  private final InvoiceMapper invoiceMapper;

  private final Encoding encoding;

  private final WebClient openAiClient;

  private final ObjectMapper objectMapper;

  private final FileStorageService fileStorageService;

  private final InvoicePdfService invoicePdfService;

  private final UserActivityService userActivityService;

  private String openAiApiKey;

  public InvoiceServiceImpl(final InvoiceRepository invoiceRepository, final LimitGuardService limitGuardService,
      final CompanyRepository companyRepository,
      final OCRService ocrService, final InvoiceMapper invoiceMapper,
      final InvoiceDocumentRepository invoiceDocumentRepository,
      final FileStorageService fileStorageService,
      @Qualifier("openAiWebClient") final WebClient openAiClient, final ObjectMapper objectMapper,
      final CompaniesRegistry subjectRegistry,
      final InvoicePdfService invoicePdfService,
      final UserActivityService userActivityService) {
    this.invoiceRepository = invoiceRepository;
    this.limitGuardService = limitGuardService;
    this.companyRepository = companyRepository;
    this.fileStorageService = fileStorageService;
    this.ocrService = ocrService;
    this.openAiClient = openAiClient;
    this.subjectRegistry = subjectRegistry;
    this.invoiceDocumentRepository = invoiceDocumentRepository;
    this.invoicePdfService = invoicePdfService;
    this.encoding = Encodings.newDefaultEncodingRegistry()
        .getEncodingForModel("gpt-4o-mini")
        .orElseThrow(() -> new IllegalArgumentException("Encoding for model not found"));
    this.invoiceMapper = invoiceMapper;
    this.objectMapper = objectMapper;
    this.userActivityService = userActivityService;
  }

  @Override
  @RequiresOwnership
  public Long saveInvoice(final MultipartFile file, final @OwnedCompany Long companyId,
      final Authentication connectedUser) {
    final Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new EntityNotFoundException("Company " + companyId + " not found"));

    limitGuardService.checkLimit(connectedUser, LimitType.INVOICE_PROCESS);

    log.info("kto vytvoril company {}", company.getCreatedBy());
    log.info("prihlaseny uzivatel {}", connectedUser.getName());

    final InvoiceRequest request;
    final String pdfText = ocrService.extractTextFromPDF(file, connectedUser);

    final int rawToken = countTokens(pdfText);
    log.info("Token count: {}", rawToken);

    final String cleanedText = removeUnwantedCharacters(pdfText);
    final int cleanedToken = countTokens(cleanedText);
    log.info("Token count after cleaning: {}", cleanedToken);

    final String openApiResponse = askOpenAi(cleanedText);

    System.out.println("Open API response: " + openApiResponse);

    // final String json = """
    //     {
    //       "Invoice Number": "10/2023/602",
    //       "Date of Issue": "15.2.2023",
    //       "Date of Delivery": "15.2.2023",
    //       "Date of Due": "1.4.2023",
    //       "Variable Symbol": "231000602",
    //       "Supplier VAT ID": "SK2024181346",
    //       "Supplier TAX ID": "2024181346",
    //       "Supplier Registration Number": "47998156",
    //       "Supplier Name": "CANIS SAFETY a.s., organizačná zložka Košice",
    //       "Invoice Items": [
    //         {
    //           "description": "Filtr 3M 6059, 1 pár",
    //           "quantity": "2",
    //           "unit price": "10,102",
    //           "total": "20"
    //         },
    //         {
    //           "description": "Štít ŠP 29",
    //           "quantity": "10",
    //           "unit price": "8,266",
    //           "total": "82,66"
    //         }
    //       ]
    //     }
    //     """;

    final int outputTokens = encoding.encode(openApiResponse).size();
    log.info("Output tokens count: {}", outputTokens);

    try {
      request = objectMapper.readValue(openApiResponse, InvoiceRequest.class);
    } catch (JacksonException e) {
      log.error("Failed to parse JSON response from OpenAI", e);
      throw new RuntimeException("Invalid JSON from OpenAI", e);
    }

    final Invoice newInvoice = invoiceMapper.toInvoice(request);
    newInvoice.setCompany(company);

    subjectRegistry.findByIco(request.partnerRegistrationNumber())
        .ifPresentOrElse(
            subjectItem -> {
              log.info("Register number {} find in registry", request.partnerRegistrationNumber());
              newInvoice.setPartnerName(subjectItem.getName());
              newInvoice.setPartnerCity(subjectItem.getCity());
              newInvoice.setPartnerStreet(subjectItem.getStreet());
              newInvoice.setPartnerZip(subjectItem.getZip());
              newInvoice.setPartnerTaxId(subjectItem.getVatId());
              newInvoice.setPartnerVatId(subjectItem.getVatId());
            },
            () -> log.warn("Register number {} is missing in registry", request.partnerRegistrationNumber())
        );

    invoiceRepository.save(newInvoice);
    uploadDocument(file, false, newInvoice.getId(), connectedUser, null);

    return newInvoice.getId();
  }

  @Override
  @RequiresOwnership
  public Long createInvoice(final InvoiceCreate invoiceRequest, final @OwnedCompany Long companyId,
      final Authentication connectedUser) {
    final Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new EntityNotFoundException("Company " + companyId + " not found"));

    final Invoice createdInvoice = invoiceMapper.toCreateInvoice(invoiceRequest, company);

    userActivityService.save(UserActivityRecord.forType(connectedUser.getName(), UserActivityType.CREATED_INVOICE,
        "User Created Invoice for Company" + company.getName()));

    invoiceRepository.save(createdInvoice);

    return createdInvoice.getId();
  }

  @Override
  @RequiresOwnership
  public InvoiceResponse findById(final @OwnedInvoice Long invoiceId) {
    return invoiceRepository.findById(invoiceId)
        .map(invoiceMapper::toInvoiceResponse)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Receipt with ID " + invoiceId + " not found."));
  }

  // @Override
  // public InvoiceImportResponseDto getInvoiceById(Long id) {
  //   return invoiceImportRepository.findById(id)
  //       .map(invoiceImport -> new InvoiceImportResponseDto(
  //           invoiceImport.getId(),
  //           invoiceImport.getImport_date(),
  //           new InvoiceDetailsDTO(
  //               invoiceImport.getInvoice_number(),
  //               invoiceImport.getVariable_symbol(),
  //               invoiceImport.getVariable_symbol(),
  //               invoiceImport.getIssue_date(),
  //               invoiceImport.getTax_date(),
  //               invoiceImport.getDue_date()
  //           ),
  //           new PartnerDto(
  //               invoiceImport.getPartner_name(),
  //               invoiceImport.getPartner_city(),
  //               invoiceImport.getPartner_street(),
  //               invoiceImport.getPartner_zip(),
  //               invoiceImport.getPartner_registration_number(),
  //               invoiceImport.getPartner_tax_id(),
  //               invoiceImport.getPartner_vat_id()
  //           ),
  //           invoiceImport.getStatus(),
  //           invoiceImport.getCompany().getId()
  //       ))
  //       .orElseThrow(() -> new IllegalArgumentException("Invoice with id " + id + " not found"));
  //   return null;
  // }

  @Override
  @RequiresOwnership
  public PageResponse<InvoiceResponseTable> findInvoicesByCompanyId(final int page, final int size,
      final @OwnedCompany Long companyId) {
    final Pageable pageable = PageableFactory.ofDescending(page, size);
    final Page<Invoice> invoices = invoiceRepository.findAll(withCompanyId(companyId), pageable);

    return PageResponse.from(invoices, invoiceMapper::toInvoiceTableResponse);
  }

  @Override
  @RequiresOwnership
  public List<DocumentTableResponse> findDocumentsByInvoiceId(final @OwnedInvoice Long invoiceId) {

    final List<InvoiceDocument> documents = invoiceDocumentRepository.findByInvoiceId(invoiceId);

    return documents.stream()
        .map(invoiceMapper::toInvoiceDocumentsTableResponse)
        .toList();
  }

  @Override
  @RequiresOwnership
  public byte[] generateInvoicePdf(final @OwnedInvoice Long invoiceId) {
    final InvoiceResponse invoice = findById(invoiceId);
    return invoicePdfService.generateInvoicePdf(invoice);
  }

  @Override
  public PageResponse<InvoiceResponseTable> findAllInvoicesByUser(final int page, final int size,
      final Authentication connectedUser) {
    final Pageable pageable = PageableFactory.ofDescending(page, size);
    final Page<Invoice> invoices = invoiceRepository.findAll(withUserId(connectedUser.getName()), pageable);

    return PageResponse.from(invoices, invoiceMapper::toInvoiceTableResponse);
  }

  @Override
  @RequiresOwnership
  public void uploadDocument(final MultipartFile document, final Boolean canDeleteDocument,
      @OwnedInvoice final Long invoiceId, final Authentication connectedUser,
      @Nullable final AddDocumentData additionalDocumentData) {

    final Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new EntityNotFoundException("No invoice found with the ID: " + invoiceId));

    final var documentToUpload = fileStorageService.saveFile(document, invoice, connectedUser.getName());

    final InvoiceDocument invoiceDocument = new InvoiceDocument();
    invoiceDocument.setInvoice(invoice);
    invoiceDocument.setFilename(document.getOriginalFilename());
    invoiceDocument.setDocument(documentToUpload);
    invoiceDocument.setCanDelete(canDeleteDocument);

    if (additionalDocumentData != null) {
      invoiceDocument.setDocumentName(additionalDocumentData.documentName());
      invoiceDocument.setNote(additionalDocumentData.note());
    } else {
      invoiceDocument.setDocumentName(document.getOriginalFilename());
    }

    userActivityService.save(
        UserActivityRecord.forType(connectedUser.getName(), UserActivityType.DOCUMENT_UPLOAD_INVOICE,
            "User added document to invoice" + invoice.getId()));

    invoiceDocumentRepository.save(invoiceDocument);
  }

  @Override
  @RequiresOwnership
  public void deleteDocument(final @OwnedInvoiceDocument Long documentId, final Authentication connectedUser) {
    final InvoiceDocument invoiceDocument = invoiceDocumentRepository.findById(documentId)
        .orElseThrow(() -> new EntityNotFoundException("No document found with the ID: " + documentId));

    if (!invoiceDocument.isCanDelete()) {
      throw new DownloadDocumentException(
          "This document cannot be deleted because it is source of data for invoice " + invoiceDocument.getInvoice()
              .getId()
      );
    }

    final Path filePath = Paths.get(invoiceDocument.getDocument());

    try {
      Files.deleteIfExists(filePath);
      log.info("Deleted file from filesystem: {}", filePath.toAbsolutePath());
    } catch (IOException e) {
      log.error("Failed to delete file: {}", filePath, e);
    }

    invoiceDocumentRepository.delete(invoiceDocument);
    userActivityService.save(
        UserActivityRecord.forType(connectedUser.getName(), UserActivityType.DOCUMENT_DELETE_INVOICE,
            "User deleted document from invoice" + invoiceDocument.getInvoice().getId() + " with name "
                + invoiceDocument.getDocumentName()));
  }

  @Override
  @RequiresOwnership
  public void deleteInvoice(final @OwnedInvoice Long invoiceId) {
    final Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new EntityNotFoundException("No invoice found with the ID: " + invoiceId));

    for (final InvoiceDocument doc : invoice.getDocuments()) {
      final Path filePath = Paths.get(doc.getDocument());
      try {
        Files.deleteIfExists(filePath);
        log.info("Deleted file from filesystem: {}", filePath.toAbsolutePath());
      } catch (IOException e) {
        log.error("Failed to delete file: {}", filePath, e);
      }
    }

    // teraz vymaž dokumenty z DB (ak nie je nastavený cascade = REMOVE)
    invoiceDocumentRepository.deleteAll(invoice.getDocuments());
    invoiceRepository.delete(invoice);
  }

  @NotNull
  private String askOpenAi(final String cleanedText) {

    final String prompt = """
        Extract the following fields from the invoice text and return them in JSON format:
        - Invoice Number
        - Date of Issue
        - Date of Delivery
        - Date of Due
        - Variable Symbol
        - Supplier Registration Number
        - Invoice Items (description, quantity, unit price, total)
        
        Return only the raw JSON without any explanation or extra text.
        The format of date should be YYYY-MM-DD
        
        Invoice text:
        """ + cleanedText;

    final Map<String, Object> requestBody = Map.of(
        "model", "gpt-4o-mini",
        "messages", List.of(Map.of("role", "user", "content", prompt))
    );

    final String gptResponse = openAiClient.post()
        .uri("/chat/completions")
        .header("Authorization", "Bearer " + openAiApiKey)
        .header("Content-Type", "application/json")
        .bodyValue(requestBody)
        .retrieve()
        .bodyToMono(JsonNode.class)

        .map(node -> node.path("choices")
            .get(0)
            .path("message")
            .path("content")
            .asText())
        .block();

    return gptResponse
        .replaceAll("^```json\\s*", "")
        .replaceAll("```$", "")
        .trim();
  }

  private String removeUnwantedCharacters(final String text) {
    String cleaned = removeOdberatel(text);

    final String noEmptyLines = Arrays.stream(cleaned.split("\\r?\\n"))
        .filter(line -> !line.trim().isEmpty())
        .collect(Collectors.joining("\n"));

    final String normalizedSpaces = noEmptyLines.replaceAll("\\s+", " ");

    cleaned = normalizedSpaces.replaceAll("[^\\p{L}\\p{N}.,:()/\\s-]", "");

    return cleaned;
  }

  private int countTokens(final String text) {
    return encoding.encode(text).size();
  }

  private String removeOdberatel(final String text) {
    return ODBERATEL_BLOCK.matcher(text).replaceAll("$2").trim();
  }

}
