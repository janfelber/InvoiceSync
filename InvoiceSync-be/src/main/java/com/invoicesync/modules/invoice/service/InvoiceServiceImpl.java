package com.invoicesync.modules.invoice.service;

import static com.invoicesync.modules.invoice.InvoiceSpecification.withCompanyId;
import static com.invoicesync.modules.invoice.InvoiceSpecification.withUserId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
import com.invoicesync.modules.invoice.extraction.ExtractionMapper;
import com.invoicesync.modules.invoice.extraction.ExtractionMode;
import com.invoicesync.modules.invoice.extraction.InvoiceExtractor;
import com.invoicesync.modules.invoice.extraction.model.dto.ExtractInvoiceResponse;
import com.invoicesync.modules.invoice.extraction.model.dto.ExtractedInvoiceData;
import com.invoicesync.modules.invoice.extraction.model.dto.ExtractionMeta;
import com.invoicesync.modules.invoice.mapper.InvoiceMapper;
import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.invoice.model.InvoiceCreate;
import com.invoicesync.modules.invoice.model.InvoiceResponse;
import com.invoicesync.modules.invoice.model.InvoiceResponseTable;
import com.invoicesync.modules.invoice.repository.InvoiceRepository;
import com.invoicesync.modules.subscription.guard.service.LimitGuardService;
import com.invoicesync.modules.user.service.UserService;
import com.invoicesync.partner.CompaniesRegistry;

import tools.jackson.databind.ObjectMapper;

@Service
public class InvoiceServiceImpl implements InvoiceService {

  private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

  final CompaniesRegistry subjectRegistry;

  private final InvoiceRepository invoiceRepository;

  private final InvoiceDocumentRepository invoiceDocumentRepository;

  private final LimitGuardService limitGuardService;

  private final CompanyRepository companyRepository;

  private final InvoiceMapper invoiceMapper;

  private final ObjectMapper objectMapper;

  private final FileStorageService fileStorageService;

  private final InvoicePdfService invoicePdfService;

  private final UserActivityService userActivityService;

  private final UserService userService;

  private final InvoiceExtractor invoiceExtractor;

  private final ExtractionMapper extractionMapper;

  public InvoiceServiceImpl(final InvoiceRepository invoiceRepository,
      final LimitGuardService limitGuardService,
      final CompanyRepository companyRepository,
      final InvoiceMapper invoiceMapper,
      final InvoiceDocumentRepository invoiceDocumentRepository,
      final FileStorageService fileStorageService,
      final ObjectMapper objectMapper,
      final CompaniesRegistry subjectRegistry,
      final InvoicePdfService invoicePdfService,
      final UserActivityService userActivityService,
      final UserService userService,
      final InvoiceExtractor invoiceExtractor,
      final ExtractionMapper extractionMapper) {
    this.invoiceRepository = invoiceRepository;
    this.limitGuardService = limitGuardService;
    this.companyRepository = companyRepository;
    this.fileStorageService = fileStorageService;
    this.subjectRegistry = subjectRegistry;
    this.invoiceDocumentRepository = invoiceDocumentRepository;
    this.invoicePdfService = invoicePdfService;
    this.invoiceMapper = invoiceMapper;
    this.objectMapper = objectMapper;
    this.userActivityService = userActivityService;
    this.userService = userService;
    this.invoiceExtractor = invoiceExtractor;
    this.extractionMapper = extractionMapper;
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
  public void save(final byte[] invoicePdf, final boolean includeItems, final Long companyId,
      final Authentication connectedUser) {

    limitGuardService.checkLimit(connectedUser, LimitType.INVOICE_PROCESS);

    final Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new EntityNotFoundException("Company " + companyId + " not found"));

    if (!company.getCreatedBy().equals(connectedUser.getName())) {
      throw new AccessDeniedException("You cannot process invoices for this company");
    }

    final ExtractionMode mode = includeItems ? ExtractionMode.WITH_ITEMS : ExtractionMode.HEADERS_ONLY;

    try {
      final String rawJson = invoiceExtractor.extract(invoicePdf, mode);
      final ExtractedInvoiceData invoiceData = objectMapper.readValue(rawJson, ExtractedInvoiceData.class);

      final List<String> warnings = new ArrayList<>();
      if (invoiceData.invoiceNumber() == null) {
        warnings.add("Missing invoice number");
      }
      if (invoiceData.issueDate() == null) {
        warnings.add("Missing issue date");
      }
      if (invoiceData.priceWithoutVAT() == null) {
        warnings.add("Missing price without vat");
      }

      final String status = warnings.isEmpty() ? "success" : "partial";
      final ExtractionMeta metaInfo = new ExtractionMeta(invoiceExtractor.getModelName(), null, warnings);
      final ExtractInvoiceResponse extractInvoiceResponse = new ExtractInvoiceResponse(status, invoiceData, metaInfo);

      System.out.println(extractInvoiceResponse);

      final InvoiceCreate invoiceCreate = extractionMapper.toInvoiceCreate(extractInvoiceResponse, company);
      final Invoice invoice = invoiceMapper.toCreateInvoice(invoiceCreate, company);

      invoiceRepository.save(invoice);
      userService.incrementUsed(connectedUser, LimitType.INVOICE_PROCESS);

    } catch (final Exception e) {
      log.warn("Invoice extraction failed", e);
      final ExtractionMeta metaInfo =
          new ExtractionMeta(invoiceExtractor.getModelName(), null, List.of(e.getMessage()));
    }
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

}
