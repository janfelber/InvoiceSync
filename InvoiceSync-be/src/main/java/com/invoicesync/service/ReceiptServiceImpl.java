package com.invoicesync.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
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
import com.invoicesync.dto.identity.PartnerDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptItemDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptRequestDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptRequestDetailsDTO;
import com.invoicesync.dto.receipt.reponse.ReceiptDetailsDTO;
import com.invoicesync.dto.receipt.reponse.ReceiptListDTO;
import com.invoicesync.dto.receipt.reponse.ReceiptResponseDetailsDTO;
import com.invoicesync.module.Company;
import com.invoicesync.module.Receipt;
import com.invoicesync.module.ReceiptItem;
import com.invoicesync.repository.CompanyRepository;
import com.invoicesync.repository.ReceiptRepository;
import com.invoicesync.repository.UserCredentialRepository;
import com.invoicesync.user.CurrentUserService;
import com.invoicesync.user.UserDemo;
import com.invoicesync.utils.ParseUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

  private final WebClient webClient;

  private UserCredentialRepository userRepository;

  private CompanyRepository companyRepository;

  private ReceiptRepository receiptRepository;

  private final CurrentUserService currentUserService;

  @Transactional
  @Override
  public void saveReceipt(final MultipartFile qrCodeImage, final Long userId, final Long companyId) {
    try {

      final String receiptId = decodeQRCode(qrCodeImage.getInputStream());

      final String response = sendPostRequest(receiptId);

      final ObjectMapper objectMapper = new ObjectMapper();
      final JsonNode rootNode = objectMapper.readTree(response);
      final ReceiptDTO receiptDto = ParseUtils.parseReceipt(rootNode);

      final UserDemo user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("Používateľ nenájdený"));
      final Company company = companyRepository.findById(companyId)
          .orElseThrow(() -> new RuntimeException("Firma nenájdená"));

      final Receipt receipt = mapToReceipt(receiptDto, user, company);
      receipt.setImportDate(new Date());
      receiptRepository.save(receipt);

    } catch (Exception e) {
      throw new RuntimeException("Chyba pri parsovaní JSON odpovede", e);
    }

  }

  @Override
  public List<ReceiptListDTO> getReceiptsByUserId(final Long userId) {
    return receiptRepository.findByUserId(userId)
        .stream()
        .map(receipt -> new ReceiptListDTO(
            receipt.getId(),
            receipt.getPartnerName(),
            receipt.getImportDate(),
            receipt.getCompany().getName(),
            receipt.getPartnerRegistrationNumber(),
            receipt.getPartnerTaxId(),
            receipt.getPartnerVatId(),
            receipt.getTotalPrice()
        ))
        .collect(Collectors.toList());
  }

  @Override
  public List<ReceiptListDTO> getReceiptsByCompanyId(final Long companyId) {
    return receiptRepository.findByCompanyId(companyId)
        .stream()
        .map(receipt -> new ReceiptListDTO(
            receipt.getId(),
            receipt.getPartnerName(),
            receipt.getImportDate(),
            receipt.getCompany().getName(),
            receipt.getPartnerRegistrationNumber(),
            receipt.getPartnerTaxId(),
            receipt.getPartnerVatId(),
            receipt.getTotalPrice()
        ))
        .collect(Collectors.toList());
  }

  @Override
  public ReceiptDetailsDTO getReceiptById(final Long receiptId) {
    return receiptRepository.findById(receiptId)
        .map(receipt -> {
          final List<ReceiptItemDTO> itemsDTO = receipt.getItems().stream()
              .map(item -> new ReceiptItemDTO(
                  item.getId(),
                  item.getAccountText(),
                  item.getName(),
                  item.getQuantity(),
                  item.getPriceWithoutVAT(),
                  item.getVatRate(),
                  item.getPriceWithVAT(),
                  item.getAccountValue()
              ))
              .collect(Collectors.toList());

          return new ReceiptDetailsDTO(
              receipt.getId(),
              new ReceiptResponseDetailsDTO(
                  receipt.getDate(),
                  receipt.getDatePayment(),
                  receipt.getDateTax(),
                  receipt.getTotalPrice(),
                  receipt.getAccounting(),
                  receipt.getClassificationVAT(),
                  receipt.getClassificationKVVAT(),
                  receipt.getDescription()
              ),
              new PartnerDTO(
                  receipt.getPartnerName(),
                  receipt.getPartnerCity(),
                  receipt.getPartnerStreet(),
                  receipt.getPartnerZip(),
                  receipt.getPartnerRegistrationNumber(),
                  receipt.getPartnerTaxId(),
                  receipt.getPartnerVatId()
              ),
              itemsDTO,
              receipt.getCompany().getId()
          );
        })
        .orElseThrow(() -> new IllegalArgumentException("Invoice with id " + receiptId + " not found"));
  }

  @Override
  public Receipt updateReceiptById(final Long receiptId, final Long userId,
      final ReceiptRequestDTO requestDTO) {

    final Receipt receipt = receiptRepository.findById(receiptId)
        .orElseThrow(() -> new EntityNotFoundException("Receipt not found"));

    final ReceiptRequestDetailsDTO details = requestDTO.getReceiptDetails();
    final PartnerDTO partner = requestDTO.getPartner();
    final List<ReceiptItemDTO> items = requestDTO.getItems();

    receipt.setDate(details.getDate());
    receipt.setDatePayment(details.getDatePayment());
    receipt.setDateTax(details.getDateTax());
    receipt.setAccounting(details.getAccountValue());
    receipt.setClassificationVAT(details.getClassificationVAT());
    receipt.setClassificationKVVAT(details.getClassificationKVVAT());
    receipt.setDescription(details.getDescription());

    receipt.setPartnerName(partner.getName());
    receipt.setPartnerCity(partner.getCity());
    receipt.setPartnerStreet(partner.getStreet());
    receipt.setPartnerZip(partner.getZip());
    receipt.setPartnerRegistrationNumber(partner.getRegistrationNumber());
    receipt.setPartnerTaxId(partner.getTaxId());
    receipt.setPartnerVatId(partner.getVatId());

    final Map<Long, ReceiptItem> existingItemsMap = receipt.getItems().stream()
        .collect(Collectors.toMap(ReceiptItem::getId, item -> item));

    for (final ReceiptItemDTO dto : items) {
      if (dto.getId() != null && existingItemsMap.containsKey(dto.getId())) {
        final ReceiptItem existingItem = existingItemsMap.get(dto.getId());

        existingItem.setAccountValue(dto.getAccountValue());
        existingItem.setAccountText(dto.getAccountText());
      }
    }

    receiptRepository.save(receipt);
    return receipt;
  }

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
    final Receipt receipt = Receipt.builder()
        .user(user)
        .company(company)
        .partnerName(receiptDto.getPartnerName())
        .partnerCity(receiptDto.getPartnerCity())
        .partnerStreet(receiptDto.getPartnerStreet())
        .partnerZip(receiptDto.getPartnerZip())
        .date(receiptDto.getDate())
        .datePayment(receiptDto.getDatePayment())
        .dateTax(receiptDto.getDateTax())
        .partnerRegistrationNumber(receiptDto.getPartnerRegistrationNumber())
        .partnerTaxId(receiptDto.getPartnerTaxId())
        .partnerVatId(receiptDto.getPartnerVatId())
        .totalPrice(receiptDto.getTotalPrice())
        .build();

    final List<ReceiptItem> items = new ArrayList<>();
    receiptDto.getItems().forEach(item -> {
      items.add(ReceiptItem.builder()
          .receipt(receipt)
          .name(item.getName())
          .priceWithoutVAT(item.getPriceWithoutVAT())
          .quantity(item.getQuantity())
          .vatRate(item.getVatRate())
          .priceWithVAT(item.getPriceWithVAT())
          .build());
    });

    receipt.setItems(items);

    return receipt;
  }

}