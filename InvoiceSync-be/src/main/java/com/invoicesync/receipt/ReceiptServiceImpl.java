package com.invoicesync.receipt;

import static com.invoicesync.receipt.dto.specification.ReceiptSpecification.withCompanyId;
import static com.invoicesync.receipt.dto.specification.ReceiptSpecification.withUserId;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.invoicesync.company.Company;
import com.invoicesync.company.CompanyRepository;
import com.invoicesync.deprecated.user.UserDemo;
import com.invoicesync.receipt.dto.ReceiptDTO;
import com.invoicesync.receipt.dto.ReceiptDetailDto;
import com.invoicesync.receipt.dto.ReceiptItemRequest;
import com.invoicesync.receipt.dto.ReceiptRequest;
import com.invoicesync.receipt.dto.ReceiptResponseDto;
import com.invoicesync.shared.common.PageResponse;
import com.invoicesync.shared.utils.ParseUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ReceiptServiceImpl implements ReceiptService {

  private final WebClient webClient;

  private final CompanyRepository companyRepository;
  //
  // private CompanyRepository companyRepository;

  private ReceiptRepository receiptRepository;

  // private final CurrentUserService currentUserService;

  private final ReceiptMapper receiptMapper;

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

      return receiptRepository.save(receipt).getId();

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

        existingItem.setAccountValue(dto.accountValue());
        existingItem.setAccountText(dto.accountText());
      }
    }

    receiptRepository.save(receipt);
    return receipt;
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

      return webClient.post()
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
      System.out.println(result.getText());
      return result.getText();
    } catch (NotFoundException e) {
      return "QR kód nebol nájdený.";
    }
  }

  private Receipt mapToReceipt(ReceiptDTO receiptDto, UserDemo user, Company company) {
    // final Receipt receipt = Receipt.builder()
    //     .user(user)
    //     .company(company)
    //     .partnerName(receiptDto.getPartnerName())
    //     .partnerCity(receiptDto.getPartnerCity())
    //     .partnerStreet(receiptDto.getPartnerStreet())
    //     .partnerZip(receiptDto.getPartnerZip())
    //     .date(receiptDto.getDate())
    //     .datePayment(receiptDto.getDatePayment())
    //     .dateTax(receiptDto.getDateTax())
    //     .partnerRegistrationNumber(receiptDto.getPartnerRegistrationNumber())
    //     .partnerTaxId(receiptDto.getPartnerTaxId())
    //     .partnerVatId(receiptDto.getPartnerVatId())
    //     .totalPrice(receiptDto.getTotalPrice())
    //     .build();
    //
    // final List<ReceiptItem> items = new ArrayList<>();
    // receiptDto.getItems().forEach(item -> {
    //   items.add(ReceiptItem.builder()
    //       .receipt(receipt)
    //       .name(item.getName())
    //       .priceWithoutVAT(item.getPriceWithoutVAT())
    //       .quantity(item.getQuantity())
    //       .vatRate(item.getVatRate())
    //       .priceWithVAT(item.getPriceWithVAT())
    //       .build());
    // });
    //
    // receipt.setItems(items);
    //
    // return receipt;

    return null;
  }

}