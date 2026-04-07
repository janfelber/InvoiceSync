package com.invoicesync.integration.google.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.core.audit.CustomUserDetails;
import com.invoicesync.integration.dto.GoogleStatusDTO;
import com.invoicesync.integration.google.service.GoogleIntegrationService;
import com.invoicesync.modules.user.model.User;

@RestController
@RequestMapping(Api.GOOGLE_INTEGRATION)
public class GoogleIntegrationController {

  @Value("${app.frontend.url}")
  private String frontendUrl;

  private final GoogleIntegrationService googleIntegrationService;

  public GoogleIntegrationController(final GoogleIntegrationService googleIntegrationService) {
    this.googleIntegrationService = googleIntegrationService;
  }

  @GetMapping(Api.GOOGLE_INTEGRATION_URL)
  public ResponseEntity<?> getAuthUrl(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    User user = userDetails.getUser();
    return ResponseEntity.ok(Map.of("url", googleIntegrationService.getAuthUrl(user.getId())));
  }

  @GetMapping(Api.GOOGLE_INTEGRATION_CALLBACK)
  public void callback(@RequestParam String code, @RequestParam String state, HttpServletResponse response)
      throws IOException {
    UUID userId = UUID.fromString(state);
    googleIntegrationService.handleCallback(code, userId);
    response.sendRedirect(frontendUrl + "/web/integrations?google=connected");
  }

  @DeleteMapping(Api.GOOGLE_INTEGRATION_DISCONNECT)
  public ResponseEntity<?> disconnect(Authentication connectedUser) {
    googleIntegrationService.disconnect(connectedUser);
    return ResponseEntity.ok(Map.of("status", "disconnected"));
  }

  @GetMapping(Api.GOOGLE_INTEGRATION_STATUS)
  public ResponseEntity<GoogleStatusDTO> status(Authentication authentication) {
    return ResponseEntity.ok(googleIntegrationService.getGoogleStatus(authentication));
  }

}