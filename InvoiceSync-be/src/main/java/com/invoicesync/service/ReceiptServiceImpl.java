package com.invoicesync.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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
import com.invoicesync.dto.receipt.pohoda.ReceiptDTO;
import com.invoicesync.module.Company;
import com.invoicesync.module.Receipt;
import com.invoicesync.repository.CompanyRepository;
import com.invoicesync.repository.ReceiptRepository;
import com.invoicesync.repository.UserCredentialRepository;
import com.invoicesync.user.CurrentUserService;
import com.invoicesync.user.UserDemo;
import com.invoicesync.utils.ParseUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

  private final WebClient webClient;

  private UserCredentialRepository userRepository;

  private CompanyRepository companyRepository;

  private ReceiptRepository receiptRepository;

  private final CurrentUserService currentUserService;

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
      receiptRepository.save(receipt);

    } catch (Exception e) {
      throw new RuntimeException("Chyba pri parsovaní JSON odpovede", e);
    }

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
      return result.getText();
    } catch (NotFoundException e) {
      return "QR kód nebol nájdený.";
    }
  }

  private Receipt mapToReceipt(ReceiptDTO receiptDto, UserDemo user, Company company) {
    return Receipt.builder()
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
        .build();
  }

}