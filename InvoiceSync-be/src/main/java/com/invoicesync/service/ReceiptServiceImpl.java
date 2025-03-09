package com.invoicesync.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                  item.getName(),
                  item.getName(),
                  item.getQuantity(),
                  item.getUnitPrice(),
                  item.getUnitPrice(),
                  item.getVatRate(),
                  null
              ))
              .collect(Collectors.toList());

          return new ReceiptDetailsDTO(
              receipt.getId(),
              new ReceiptResponseDetailsDTO(
                  receipt.getDate(),
                  receipt.getDatePayment(),
                  receipt.getDateTax(),
                  receipt.getTotalPrice()
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
          .unitPrice(item.getUnitPrice())
          .quantity(item.getQuantity())
          .vatRate(item.getVatRate())
          .build());
    });

    receipt.setItems(items);

    return receipt;
  }

}