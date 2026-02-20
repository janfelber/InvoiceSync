package com.invoicesync.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.modules.auth.model.MobileQrResponse;
import com.invoicesync.modules.auth.model.MobileVerifyRequest;
import com.invoicesync.modules.auth.model.MobileVerifyResponse;
import com.invoicesync.modules.auth.service.MobileAuthService;

@RestController
@RequestMapping(Api.AUTH_MOBILE)
public class MobileAuthController {

  private final MobileAuthService mobileAuthService;

  public MobileAuthController(MobileAuthService mobileAuthService) {
    this.mobileAuthService = mobileAuthService;
  }

  @GetMapping(Api.AUTH_MOBILE_QR)
  public ResponseEntity<MobileQrResponse> generateQr(Authentication connectedUser) {
    return ResponseEntity.ok(mobileAuthService.generateQrSession(connectedUser));
  }

  @PostMapping(Api.AUTH_MOBILE_VERIFY)
  public ResponseEntity<MobileVerifyResponse> verify(
      @RequestBody MobileVerifyRequest request) {
    return ResponseEntity.ok(mobileAuthService.verifyAndCreateApiKey(request));
  }

}
