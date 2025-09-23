package com.invoicesync.qrlogin;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qr-code")
public class QRLoginController {

  private final QRLoginService qrLoginService;

  private final QRLoginRepository qrLoginRepository;

  public QRLoginController(final QRLoginService qrLoginService, final QRLoginRepository qrLoginRepository) {
    this.qrLoginService = qrLoginService;
    this.qrLoginRepository = qrLoginRepository;
  }

  @PostMapping("/login/generate")
  public String generateQrToken() {
    return qrLoginService.createQrToken();
  }

  @GetMapping("/login/status")
  public QrLoginStatusResponse getStatus(@RequestParam String token) {
    QRLogin qrLogin = qrLoginRepository.findByToken(token)
        .orElseThrow();

    if (qrLogin.getExpiresAt().isBefore(Instant.now())) {
      return new QrLoginStatusResponse("EXPIRED", null, null);
    }

    return new QrLoginStatusResponse(qrLogin.getStatus(), qrLogin.getToken(), qrLogin.getUserId());
  }

  @PostMapping("/login/confirm")
  public ResponseEntity<QrLoginStatusResponse> confirmQrLogin(
      @RequestBody QrLoginConfirmRequest request
  ) {
    System.out.println("Confirm QR Login: " + request);

    QRLogin qrLogin = qrLoginRepository.findByToken(request.token())
        .orElseThrow(() -> new RuntimeException("Token not found"));

    if (qrLogin.getExpiresAt().isBefore(Instant.now())) {
      return ResponseEntity.ok(new QrLoginStatusResponse("EXPIRED", null, null));
    }

    // nastavíme stav ako potvrdený
    qrLogin.setToken(request.accessToken()); // alebo ho získaš z Keycloaku
    qrLogin.setUserId(request.userId());
    qrLoginRepository.save(qrLogin);

    return ResponseEntity.ok(
        new QrLoginStatusResponse("CONFIRMED", request.accessToken(), request.userId())
    );
  }

}
