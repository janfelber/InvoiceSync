package com.invoicesync.integration.google.service;

import java.io.IOException;
import java.util.UUID;

public interface GoogleIntegrationService {

  String getAuthUrl(UUID userId);

  void handleCallback(String code, UUID userId) throws IOException;

  void disconnect(UUID userId);

}
