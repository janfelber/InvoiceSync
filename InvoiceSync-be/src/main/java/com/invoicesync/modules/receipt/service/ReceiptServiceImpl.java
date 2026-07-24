package com.invoicesync.modules.receipt.service;

import static com.invoicesync.modules.receipt.dto.specification.ReceiptSpecification.withCompanyId;
import static com.invoicesync.modules.receipt.dto.specification.ReceiptSpecification.withUserId;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.common.PageableFactory;
import com.invoicesync.core.exception.DownloadDocumentException;
import com.invoicesync.core.filestorage.service.FileStorageService;
import com.invoicesync.core.utils.ParseUtils;
import com.invoicesync.modules.activity.model.UserActivityType;
import com.invoicesync.modules.activity.service.UserActivityRecord;
import com.invoicesync.modules.activity.service.UserActivityService;
import com.invoicesync.modules.auth.security.OwnedCompany;
import com.invoicesync.modules.auth.security.OwnedReceipt;
import com.invoicesync.modules.auth.security.OwnedReceiptDocument;
import com.invoicesync.modules.auth.security.RequiresOwnership;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.company.repository.CompanyRepository;
import com.invoicesync.modules.document.model.AddDocumentData;
import com.invoicesync.modules.document.model.DocumentTableResponse;
import com.invoicesync.modules.document.model.ReceiptDocument;
import com.invoicesync.modules.document.repository.ReceiptDocumentRepository;
import com.invoicesync.modules.receipt.counter.UserReceiptCounterRepository;
import com.invoicesync.modules.receipt.mapper.ReceiptMapper;
import com.invoicesync.modules.receipt.model.MergeItemsRequest;
import com.invoicesync.modules.receipt.model.Receipt;
import com.invoicesync.modules.receipt.model.ReceiptDetailDto;
import com.invoicesync.modules.receipt.model.ReceiptItem;
import com.invoicesync.modules.receipt.model.ReceiptItemRequest;
import com.invoicesync.modules.receipt.model.ReceiptRequest;
import com.invoicesync.modules.receipt.model.ReceiptResponseDto;
import com.invoicesync.modules.receipt.repository.ReceiptRepository;

@Service
public class ReceiptServiceImpl implements ReceiptService {

  @Qualifier("ekasaWebClient")
  private final WebClient ekasaClient;

  private final ReceiptMapper receiptMapper;

  private final FileStorageService fileStorageService;

  private final ReceiptRepository receiptRepository;

  private final ReceiptDocumentRepository documentRepository;

  private final ReceiptDocumentRepository receiptDocumentRepository;

  private final UserReceiptCounterRepository userReceiptCounterRepository;

  private final UserActivityService userActivityService;

  private final CompanyRepository companyRepository;

  @Autowired
  public ReceiptServiceImpl(
      @Qualifier("ekasaWebClient") final WebClient ekasaClient,
      final ReceiptMapper receiptMapper,
      final FileStorageService fileStorageService,
      final ReceiptRepository receiptRepository,
      final ReceiptDocumentRepository documentRepository,
      final ReceiptDocumentRepository receiptDocumentRepository,
      final UserReceiptCounterRepository userReceiptCounterRepository,
      final UserActivityService userActivityService, final CompanyRepository companyRepository) {
    this.ekasaClient = ekasaClient;
    this.receiptMapper = receiptMapper;
    this.fileStorageService = fileStorageService;
    this.receiptRepository = receiptRepository;
    this.documentRepository = documentRepository;
    this.receiptDocumentRepository = receiptDocumentRepository;
    this.userReceiptCounterRepository = userReceiptCounterRepository;
    this.userActivityService = userActivityService;
    this.companyRepository = companyRepository;
  }

  @Override
  public PageResponse<ReceiptResponseDto> findAllReceiptsByUser(final int page, final int size,
      final Authentication connectedUser) {
    final Pageable pageable = PageableFactory.ofDescending(page, size);
    final Page<Receipt> receipts = receiptRepository.findAll(withUserId(connectedUser.getName()), pageable);

    return PageResponse.from(receipts, receiptMapper::toReceiptTableResponse);
  }

  @Override
  @RequiresOwnership
  public PageResponse<ReceiptResponseDto> findReceiptsByCompanyId(final int page, final int size,
      final @OwnedCompany Long companyId) {
    final Pageable pageable = PageableFactory.ofDescending(page, size);
    final Page<Receipt> receipts = receiptRepository.findAll(withCompanyId(companyId), pageable);

    return PageResponse.from(receipts, receiptMapper::toReceiptTableResponse);
  }

  @Override
  @RequiresOwnership
  public ReceiptDetailDto findById(final @OwnedReceipt long receiptId) {
    return receiptRepository.findById(receiptId)
        .map(receiptMapper::toReceiptResponse)
        .orElseThrow();
  }

  @Transactional
  @Override
  @RequiresOwnership
  public Long saveReceipt(final MultipartFile qrCodeImage, final @OwnedCompany Long companyId,
      final Authentication connectedUser) {
    try {

      final String receiptId = decodeQRCode(qrCodeImage.getInputStream());
      System.out.println(receiptId);

      final String response = sendPostRequest(receiptId);

      final ObjectMapper objectMapper = new ObjectMapper();
      final JsonNode rootNode = objectMapper.readTree(response);
      final ReceiptRequest request = ParseUtils.parseReceiptRequest(rootNode, companyId);

      final Receipt receipt = receiptMapper.toReceipt(request);
      final Long nextOrder = userReceiptCounterRepository.getAndIncrementReceiptOrder(connectedUser.getName());
      receipt.setReceiptOrder(nextOrder);

      receiptRepository.save(receipt);
      uploadReceiptDocument(qrCodeImage, false, receipt.getId(), connectedUser, null);
      return receipt.getId();
    } catch (Exception e) {
      throw new RuntimeException("Chyba pri parsovaní JSON odpovede", e);
    }

  }

  @Override
  @RequiresOwnership
  public Receipt updateReceiptById(final @OwnedReceipt Long receiptId, final ReceiptRequest request) {

    final Receipt receipt = receiptRepository.findById(receiptId)
        .orElseThrow(() -> new EntityNotFoundException("Receipt not found"));

    final List<ReceiptItemRequest> items = request.items();

    receipt.setDate(request.date());
    receipt.setPaymentDate(request.datePayment());
    receipt.setTaxDate(request.dateTax());
    receipt.setAccountValue(request.accounting());
    receipt.setPaymentType(request.paymentType());
    receipt.setVatClassification(request.classificationVAT());
    receipt.setKvVatClassification(request.classificationKVVAT());
    receipt.setDescription(request.description());

    receipt.setSupplierName(request.partnerName());
    receipt.setSupplierCity(request.partnerCity());
    receipt.setSupplierStreet(request.partnerStreet());
    receipt.setSupplierZip(request.partnerZip());
    receipt.setSupplierRegistrationNumber(request.partnerRegistrationNumber());
    receipt.setSupplierTaxId(request.partnerTaxId());
    receipt.setSupplierVatId(request.partnerVatId());

    final Map<Long, ReceiptItem> existingItemsMap = receipt.getItems().stream()
        .collect(Collectors.toMap(ReceiptItem::getId, item -> item));

    for (final ReceiptItemRequest dto : items) {
      if (dto.id() != null && existingItemsMap.containsKey(dto.id())) {
        final ReceiptItem existingItem = existingItemsMap.get(dto.id());

        // unitPriceWithVat is the source of truth — always recompute derived prices
        // from the incoming dto values so any combination of changes (price, vatRate, quantity) is handled correctly
        final BigDecimal unitPriceWithoutVat = dto.unitPriceWithVat()
            .divide(BigDecimal.valueOf(1 + dto.vatRate() / 100.0), 2, RoundingMode.HALF_UP);

        existingItem.setName(dto.name());
        existingItem.setQuantity(dto.quantity());
        existingItem.setVatRate(dto.vatRate());
        existingItem.setUnitPriceWithVat(dto.unitPriceWithVat());
        existingItem.setUnitPriceWithoutVat(unitPriceWithoutVat);
        existingItem.setTotalItemPriceWithoutVat(unitPriceWithoutVat.multiply(BigDecimal.valueOf(dto.quantity())));
        existingItem.setTotalItemPriceWithVat(dto.unitPriceWithVat().multiply(BigDecimal.valueOf(dto.quantity())));
        existingItem.setAccountValue(dto.accountValue());
        existingItem.setAccountText(dto.accountText());
      }
    }

    final BigDecimal newTotalWithVat = receipt.getItems().stream()
        .map(ReceiptItem::getTotalItemPriceWithVat)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    final BigDecimal newTotalWithoutVat = receipt.getItems().stream()
        .map(ReceiptItem::getTotalItemPriceWithoutVat)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    receipt.setTotalPriceWithVat(newTotalWithVat);
    receipt.setTotalPriceWithoutVat(newTotalWithoutVat);

    receiptRepository.save(receipt);
    return receipt;
  }

  @Override
  @RequiresOwnership
  public void uploadReceiptDocument(final MultipartFile document, final Boolean canDeleteDocument,
      final @OwnedReceipt Long receiptId,
      final Authentication connectedUser, @Nullable final AddDocumentData additionalDocumentData) {

    final Receipt receipt = receiptRepository.findById(receiptId)
        .orElseThrow(() -> new EntityNotFoundException("No receipt found with id: " + receiptId));

    final var documentToUpload = fileStorageService.saveReceiptFile(document, receipt, connectedUser.getName());

    final ReceiptDocument receiptDocument = new ReceiptDocument();
    receiptDocument.setReceipt(receipt);
    receiptDocument.setFilename(document.getOriginalFilename());
    receiptDocument.setDocument(documentToUpload);
    receiptDocument.setCanDelete(canDeleteDocument);

    if (additionalDocumentData != null) {
      receiptDocument.setDocumentName(additionalDocumentData.documentName());
      receiptDocument.setNote(additionalDocumentData.note());
    } else {
      receiptDocument.setDocumentName(document.getOriginalFilename());
    }

    userActivityService.save(
        UserActivityRecord.forType(connectedUser.getName(), UserActivityType.DOCUMENT_UPLOAD_RECEIPT,
            "User added document to receipt" + receipt.getId()));
    documentRepository.save(receiptDocument);
  }

  @Override
  @RequiresOwnership
  public void deleteReceiptDocument(final @OwnedReceiptDocument Long documentId, final Authentication connectedUser) {
    final ReceiptDocument receiptDocument = receiptDocumentRepository.findById(documentId)
        .orElseThrow(() -> new EntityNotFoundException("Receipt document not found"));

    if (!receiptDocument.isCanDelete()) {
      throw new DownloadDocumentException(
          "This document cannot be deleted because it is source of data for invoice " + receiptDocument.getReceipt()
              .getId()
      );
    }

    receiptDocumentRepository.deleteById(documentId);
    userActivityService.save(
        UserActivityRecord.forType(connectedUser.getName(), UserActivityType.DOCUMENT_DELETE_RECEIPT,
            "User deleted from receipt" + receiptDocument.getReceipt().getId() + "with name "
                + receiptDocument.getDocumentName()));
  }

  @Override
  @RequiresOwnership
  public List<DocumentTableResponse> findDocumentsByReceipt(final @OwnedReceipt Long receiptId) {
    final List<ReceiptDocument> documents = documentRepository.findByReceiptId(receiptId);

    return documents.stream()
        .map(receiptMapper::receiptDocumentTableResponse)
        .toList();
  }

  @Override
  @RequiresOwnership
  public void deleteReceiptById(final @OwnedReceipt Long receiptId, final Authentication connectedUser) {
    receiptRepository.deleteById(receiptId);
    userActivityService.save(UserActivityRecord.forType(connectedUser.getName(), UserActivityType.DELETE_RECEIPT,
        "User deleted receipt with id" + receiptId));
  }

  @Override
  @RequiresOwnership
  public void mergeReceiptItems(final @OwnedReceipt Long receiptId, final MergeItemsRequest request) {

    final Receipt receipt = receiptRepository.findById(receiptId)
        .orElseThrow(() -> new EntityNotFoundException("Receipt not found"));

    final List<ReceiptItem> itemsToMerge = receipt.getItems().stream()
        .filter(item -> request.itemIds().contains(item.getId()))
        .toList();

    if (itemsToMerge.size() < 2) {
      throw new IllegalArgumentException("At least two items must be selected for merging.");
    }

    final BigDecimal unitPriceWithVat = itemsToMerge.stream()
        .map(ReceiptItem::getUnitPriceWithVat)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    final BigDecimal unitPriceWithoutVat = itemsToMerge.stream()
        .map(ReceiptItem::getUnitPriceWithoutVat)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    final ReceiptItem mergedItem = new ReceiptItem();
    mergedItem.setReceipt(receipt);
    mergedItem.setName(request.description());
    mergedItem.setAccountText(itemsToMerge.get(0).getAccountText());
    mergedItem.setAccountValue(itemsToMerge.get(0).getAccountValue());
    mergedItem.setQuantity(1);
    mergedItem.setVatRate(itemsToMerge.get(0).getVatRate());
    mergedItem.setUnitPriceWithVat(unitPriceWithVat);
    mergedItem.setUnitPriceWithoutVat(unitPriceWithoutVat);

    // After merging, quantity is always 1, so unit price equals total price.
    // Both fields hold the same value: the sum of all merged items' totals.
    mergedItem.setTotalItemPriceWithVat(unitPriceWithVat);
    mergedItem.setTotalItemPriceWithoutVat(unitPriceWithoutVat);

    receipt.getItems().removeAll(itemsToMerge);
    receipt.getItems().add(mergedItem);

    receiptRepository.save(receipt);
  }

  @Override
  @RequiresOwnership
  public void reassignReceipt(final @OwnedReceipt Long receiptId, final @OwnedCompany Long newCompanyId) {
    final Receipt receiptToChange = receiptRepository.findById(receiptId)
        .orElseThrow(() -> new EntityNotFoundException("Receipt not found"));

    final Company newCompany = companyRepository.findById(newCompanyId)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    receiptToChange.setCompany(newCompany);
    receiptRepository.save(receiptToChange);
  }

  // @Override
  // public ReceiptDetailsDTO findById(final Long receiptId) {
  //   return receiptRepository.findById(receiptId)
  //       .map(receipt -> {
  //         final List<ReceiptItemDTO> itemsDTO = receipt.getItems().stream()
  //             .map(item -> new ReceiptItemDTO(
  //                 item.getId(),
  //                 item.getAccountText(),
  //                 item.getName(),
  //                 item.getQuantity(),
  //                 item.getPriceWithoutVAT(),
  //                 item.getVatRate(),
  //                 item.getPriceWithVAT(),
  //                 item.getAccountValue()
  //             ))
  //             .collect(Collectors.toList());
  //
  //         return new ReceiptDetailsDTO(
  //             receipt.getId(),
  //             new ReceiptResponseDetailsDTO(
  //                 receipt.getDate(),
  //                 receipt.getDatePayment(),
  //                 receipt.getDateTax(),
  //                 receipt.getTotalPrice(),
  //                 receipt.getAccounting(),
  //                 receipt.getClassificationVAT(),
  //                 receipt.getClassificationKVVAT(),
  //                 receipt.getDescription()
  //             ),
  //             new PartnerDTO(
  //                 receipt.getPartnerName(),
  //                 receipt.getPartnerCity(),
  //                 receipt.getPartnerStreet(),
  //                 receipt.getPartnerZip(),
  //                 receipt.getPartnerRegistrationNumber(),
  //                 receipt.getPartnerTaxId(),
  //                 receipt.getPartnerVatId()
  //             ),
  //             itemsDTO,
  //             receipt.getCompany().getId()
  //         );
  //       })
  //       .orElseThrow(() -> new IllegalArgumentException("Invoice with id " + receiptId + " not found"));
  // }

  public String sendPostRequest(final String receiptId) {
    try {

      return ekasaClient.post()
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue("{ \"receiptId\": \"" + receiptId + "\" }")
          .retrieve()
          .bodyToMono(String.class)
          .block();
    } catch (Exception e) {
      throw new RuntimeException("Chyba pri posielaní POST požiadavky", e);
    }
  }

  //
  private String decodeQRCode(final InputStream qrCodeStream) throws IOException {
    final BufferedImage bufferedImage = ImageIO.read(qrCodeStream);
    final LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
    final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    try {
      final Result result = new MultiFormatReader().decode(bitmap);
      return result.getText();
    } catch (NotFoundException e) {
      return "QR kód nebol nájdený.";
    }
  }

}