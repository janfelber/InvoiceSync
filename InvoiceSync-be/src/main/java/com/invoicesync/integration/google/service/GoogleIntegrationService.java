package com.invoicesync.integration.google.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;

public interface GoogleIntegrationService {

  String getAuthUrl(UUID userId);

  void handleCallback(String code, UUID userId) throws IOException;

  void disconnect(UUID userId);

  void sendInvoiceEmails(List<Long> invoiceIds, Authentication connectedUser) throws Exception;

}
