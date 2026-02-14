package com.invoicesync.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.modules.auth.model.MobileQrResponse;
import com.invoicesync.modules.auth.model.MobileVerifyRequest;
import com.invoicesync.modules.auth.model.MobileVerifyResponse;
import com.invoicesync.modules.auth.service.MobileAuthService;

@RestController
@RequestMapping("/auth/mobile")
public class MobileAuthController {

  private final MobileAuthService mobileAuthService;

  public MobileAuthController(MobileAuthService mobileAuthService) {
    this.mobileAuthService = mobileAuthService;
  }

  @GetMapping("/qr")
  public ResponseEntity<MobileQrResponse> generateQr(Authentication connectedUser) {
    return ResponseEntity.ok(mobileAuthService.generateQrSession(connectedUser));
  }

  @PostMapping("/verify")
  public ResponseEntity<MobileVerifyResponse> verify(
      @RequestBody MobileVerifyRequest request) {
    return ResponseEntity.ok(mobileAuthService.verifyAndCreateApiKey(request));
  }

}
