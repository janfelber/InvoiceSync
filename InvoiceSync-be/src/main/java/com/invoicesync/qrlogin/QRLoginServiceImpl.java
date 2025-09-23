package com.invoicesync.qrlogin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jakarta.transaction.Transactional;

@Service
public class QRLoginServiceImpl implements QRLoginService{

  private final QRLoginRepository qrLoginRepository;

  public QRLoginServiceImpl(final QRLoginRepository qrLoginRepository) {
    this.qrLoginRepository = qrLoginRepository;
  }

  @Override
  public String createQrToken() {
    String token = UUID.randomUUID().toString();

    // 2. Uložíme QRLogin do DB
    QRLogin qrLogin = QRLogin.builder()
        .token(token)
        .status("PENDING")
        .createdAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(300)) // platný 5 minút
        .build();

    qrLoginRepository.save(qrLogin);

    // 3. Vytvoríme QR kód (môže obsahovať iba token alebo celé URL)
    String qrContent = token;

    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = null;
    try {
      bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);
    } catch (WriterException e) {
      throw new RuntimeException(e);
    }

    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
    try {
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    byte[] pngData = pngOutputStream.toByteArray();

    // 4. Vrátime ako Base64 string
    return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
  }

  public Optional<QRLogin> validateToken(String token) {
    Optional<QRLogin> qrLoginOpt = qrLoginRepository.findByToken(token);

    if (qrLoginOpt.isEmpty()) return Optional.empty();

    QRLogin qrLogin = qrLoginOpt.get();

    // skontroluj, či už nebol použitý
    if (qrLogin.getConsumedAt() != null) return Optional.empty();

    // skontroluj, či nevypršal
    if (qrLogin.getExpiresAt().isBefore(Instant.now())) return Optional.empty();

    return Optional.of(qrLogin);
  }

  /**
   * Označí QR token ako spotrebovaný
   */
  @Transactional
  public void consumeToken(QRLogin qrLogin) {
    qrLogin.setConsumedAt(Instant.now());
    qrLogin.setStatus("CONSUMED");
    qrLoginRepository.save(qrLogin);
  }

}
