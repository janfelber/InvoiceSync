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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

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
import com.invoicesync.core.exception.DownloadDocumentException;
import com.invoicesync.core.filestorage.service.FileStorageService;
import com.invoicesync.core.utils.ParseUtils;
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

  // private final CurrentUserService currentUserService;

  @Autowired
  public ReceiptServiceImpl(
      @Qualifier("ekasaWebClient") final WebClient ekasaClient,
      final ReceiptMapper receiptMapper,
      final FileStorageService fileStorageService,
      final ReceiptRepository receiptRepository,
      final ReceiptDocumentRepository documentRepository,
      final ReceiptDocumentRepository receiptDocumentRepository,
      final UserReceiptCounterRepository userReceiptCounterRepository) {
    this.ekasaClient = ekasaClient;
    this.receiptMapper = receiptMapper;
    this.fileStorageService = fileStorageService;
    this.receiptRepository = receiptRepository;
    this.documentRepository = documentRepository;
    this.receiptDocumentRepository = receiptDocumentRepository;
    this.userReceiptCounterRepository = userReceiptCounterRepository;
  }

  @Override
  public PageResponse<ReceiptResponseDto> findAllReceiptsByUser(final int page, final int size,
      final Authentication connectedUser) {
    final Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    final Page<Receipt> receipts = receiptRepository.findAll(withUserId(connectedUser.getName()), pageable);

    final List<ReceiptResponseDto> receiptResponse = receipts.stream()
        .map(receiptMapper::toReceiptTableResponse)
        .toList();
    return new PageResponse<>(
        receiptResponse,
        receipts.getNumber(),
        receipts.getSize(),
        receipts.getTotalElements(),
        receipts.getTotalPages(),
        receipts.isFirst(),
        receipts.isLast()
    );
  }

  @Override
  public PageResponse<ReceiptResponseDto> findReceiptsByCompanyId(final int page, final int size, final Long companyId,
      final Authentication connectedUser) {
    final Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    final Page<Receipt> receipts = receiptRepository.findAll(withCompanyId(companyId), pageable);

    final List<ReceiptResponseDto> receiptResponse = receipts.stream()
        .map(receiptMapper::toReceiptTableResponse)
        .toList();
    return new PageResponse<>(
        receiptResponse,
        receipts.getNumber(),
        receipts.getSize(),
        receipts.getTotalElements(),
        receipts.getTotalPages(),
        receipts.isFirst(),
        receipts.isLast()
    );
  }

  @Override
  public ReceiptDetailDto findById(final long receiptId) {
    System.out.println(receiptId);
    return receiptRepository.findById(receiptId)
        .map(receiptMapper::toReceiptResponse)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Receipt with ID " + receiptId + " not found."));
  }

  @Transactional
  @Override
  public Long saveReceipt(final MultipartFile qrCodeImage, final Long companyId, final Authentication connectedUser) {
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
  public Receipt updateReceiptById(final Long receiptId, final ReceiptRequest request) {

    final Receipt receipt = receiptRepository.findById(receiptId)
        .orElseThrow(() -> new EntityNotFoundException("Receipt not found"));

    final List<ReceiptItemRequest> items = request.items();

    receipt.setDate(request.date());
    receipt.setDatePayment(request.datePayment());
    receipt.setDateTax(request.dateTax());
    receipt.setAccounting(request.accounting());
    receipt.setPaidByCard(request.isPaidByCard());
    receipt.setClassificationVAT(request.classificationVAT());
    receipt.setClassificationKVVAT(request.classificationKVVAT());
    receipt.setDescription(request.description());

    receipt.setPartnerName(request.partnerName());
    receipt.setPartnerCity(request.partnerCity());
    receipt.setPartnerStreet(request.partnerStreet());
    receipt.setPartnerZip(request.partnerZip());
    receipt.setPartnerRegistrationNumber(request.partnerRegistrationNumber());
    receipt.setPartnerTaxId(request.partnerTaxId());
    receipt.setPartnerVatId(request.partnerVatId());

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
  public void uploadReceiptDocument(final MultipartFile document, final Boolean canDeleteDocument,
      final Long receiptId,
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

    documentRepository.save(receiptDocument);
  }

  @Override
  public void deleteReceiptDocument(final Long documentId, final Authentication connectedUser) {
    final ReceiptDocument receiptDocument = receiptDocumentRepository.findById(documentId)
        .orElseThrow(() -> new EntityNotFoundException("Receipt document not found"));

    if (!receiptDocument.isCanDelete()) {
      throw new DownloadDocumentException(
          "This document cannot be deleted because it is source of data for invoice " + receiptDocument.getReceipt()
              .getId()
      );
    }

    if (receiptDocument.getReceipt().getCreatedBy() != null && receiptDocument.getReceipt().getCreatedBy()
        .equals(connectedUser.getName())) {
      receiptDocumentRepository.deleteById(documentId);
    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this document.");
    }
  }

  @Override
  public List<DocumentTableResponse> findDocumentsByReceipt(final Long receiptId, final Authentication connectedUser) {
    final Receipt receipt = receiptRepository.findById(receiptId)
        .orElseThrow(() -> new EntityNotFoundException("No receipt found with id: " + receiptId));

    if (!receipt.getCreatedBy().equals(connectedUser.getName())) {
      throw new AccessDeniedException("You cannot access documents of this receipt.");
    }

    final List<ReceiptDocument> documents = documentRepository.findByReceiptId(receiptId);

    return documents.stream()
        .map(receiptMapper::receiptDocumentTableResponse)
        .toList();
  }

  @Override
  public void deleteReceiptById(final Long receiptId, final Authentication connectedUser) {
    final Receipt deleteReceipt = receiptRepository.findById(receiptId)
        .orElseThrow(() -> new EntityNotFoundException("Receipt not found"));

    if (deleteReceipt.getCreatedBy() != null && deleteReceipt.getCreatedBy().equals(connectedUser.getName())) {
      receiptRepository.deleteById(receiptId);
    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this receipt.");
    }
  }

  @Override
  public void mergeReceiptItems(final Long receiptId, final MergeItemsRequest request,
      final Authentication connectedUser) {

    final Receipt receipt = receiptRepository.findById(receiptId)
        .orElseThrow(() -> new EntityNotFoundException("Receipt not found"));

    if (!receipt.getCreatedBy().equals(connectedUser.getName())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to modify this receipt.");
    }

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