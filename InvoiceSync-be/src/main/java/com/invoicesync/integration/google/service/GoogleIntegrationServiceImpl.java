package com.invoicesync.integration.google.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.invoicesync.core.audit.CustomUserDetails;
import com.invoicesync.integration.dto.GoogleStatusDTO;
import com.invoicesync.modules.invoice.model.InvoiceResponse;
import com.invoicesync.modules.invoice.service.InvoicePdfService;
import com.invoicesync.modules.invoice.service.InvoiceService;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class GoogleIntegrationServiceImpl implements GoogleIntegrationService {

  @Value("${google.client-id}")
  private String clientId;

  @Value("${google.client-secret}")
  private String clientSecret;

  @Value("${google.redirect-uri}")
  private String redirectUri;

  private final UserRepository userRepository;

  private final InvoiceService invoiceService;

  private final InvoicePdfService invoicePdfService;

  private final OkHttpClient httpClient = new OkHttpClient();

  private final ObjectMapper objectMapper = new ObjectMapper();

  public GoogleIntegrationServiceImpl(
      final UserRepository userRepository,
      final InvoiceService invoiceService,
      final InvoicePdfService invoicePdfService) {
    this.userRepository = userRepository;
    this.invoiceService = invoiceService;
    this.invoicePdfService = invoicePdfService;
  }

  @Override
  public String getAuthUrl(UUID userId) {
    return "https://accounts.google.com/o/oauth2/v2/auth?" +
        "client_id=" + clientId +
        "&redirect_uri=" + redirectUri +
        "&response_type=code" +
        "&scope=https://www.googleapis.com/auth/gmail.send email profile" +
        "&access_type=offline" +
        "&prompt=consent" +
        "&state=" + userId.toString();
  }

  @Override
  public void handleCallback(String code, UUID userId) throws IOException {
    GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
        new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        clientId,
        clientSecret,
        code,
        redirectUri
    ).execute();

    String googleEmail = tokenResponse.parseIdToken().getPayload().getEmail();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setGoogleAccessToken(tokenResponse.getAccessToken());
    user.setGoogleRefreshToken(tokenResponse.getRefreshToken());
    user.setGoogleEmail(googleEmail);
    user.setGoogleConnected(true);

    userRepository.save(user);
  }

  @Override
  public void disconnect(Authentication connectedUser) {
    User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();

    user.setGoogleAccessToken(null);
    user.setGoogleRefreshToken(null);
    user.setGoogleEmail(null);
    user.setGoogleConnected(false);

    userRepository.save(user);
  }

  @Override
  public void sendInvoiceEmails(List<Long> invoiceIds, Authentication connectedUser) throws Exception {
    User user = userRepository.findById(UUID.fromString(connectedUser.getName()))
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (!user.isGoogleConnected()) {
      throw new RuntimeException("Google account is not connected");
    }

    String accessToken = user.getGoogleAccessToken();

    for (Long invoiceId : invoiceIds) {
      InvoiceResponse invoice = invoiceService.findById(invoiceId);
      String emailTo = invoice.getCompany().getEmail();

      System.out.println("Email to: " + emailTo);

      if (emailTo == null || emailTo.isBlank()) {
        continue;
      }

      byte[] pdfBytes = invoicePdfService.generateInvoicePdf(invoice);
      String filename = "invoice-" + invoiceId + ".pdf";
      String subject = "Invoice " + invoice.getInvoiceDetails().getInvoiceNumber();
      String body = "Please find the attached invoice.";

      try {
        sendGmail(accessToken, emailTo, subject, body, pdfBytes, filename);
      } catch (RuntimeException e) {
        if ("UNAUTHORIZED".equals(e.getMessage())) {
          accessToken = refreshAccessToken(user);
          sendGmail(accessToken, emailTo, subject, body, pdfBytes, filename);
        } else {
          throw e;
        }
      }
    }
  }

  @Override
  public GoogleStatusDTO getGoogleStatus(final Authentication connectedUser) {
    User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
    return new GoogleStatusDTO(user.isGoogleConnected(), user.getGoogleEmail() != null ? user.getGoogleEmail() : "");
  }

  private void sendGmail(String accessToken, String to, String subject, String bodyText, byte[] pdfBytes,
      String pdfFilename) throws IOException {
    String boundary = "boundary_" + System.currentTimeMillis();

    final String mime = "To: " + to + "\r\n"
        + "Subject: " + subject + "\r\n"
        + "MIME-Version: 1.0\r\n"
        + "Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n\r\n"
        + "--" + boundary + "\r\n"
        + "Content-Type: text/plain; charset=UTF-8\r\n\r\n"
        + bodyText + "\r\n\r\n"
        + "--" + boundary + "\r\n"
        + "Content-Type: application/pdf\r\n"
        + "Content-Transfer-Encoding: base64\r\n"
        + "Content-Disposition: attachment; filename=\"" + pdfFilename + "\"\r\n\r\n"
        + Base64.getEncoder().encodeToString(pdfBytes) + "\r\n"
        + "--" + boundary + "--";

    String raw =
        Base64.getUrlEncoder().withoutPadding().encodeToString(mime.getBytes(StandardCharsets.UTF_8));

    String json = "{\"raw\":\"" + raw + "\"}";

    Request request = new Request.Builder()
        .url("https://gmail.googleapis.com/gmail/v1/users/me/messages/send")
        .addHeader("Authorization", "Bearer " + accessToken)
        .post(RequestBody.create(json, MediaType.parse("application/json")))
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (response.code() == 401) {
        throw new RuntimeException("UNAUTHORIZED");
      }
      if (!response.isSuccessful()) {
        throw new RuntimeException("Gmail API error: " + response.code());
      }
    }
  }

  private String refreshAccessToken(User user) throws IOException {
    String body = "client_id=" + clientId +
        "&client_secret=" + clientSecret +
        "&refresh_token=" + user.getGoogleRefreshToken() +
        "&grant_type=refresh_token";

    Request request = new Request.Builder()
        .url("https://oauth2.googleapis.com/token")
        .post(RequestBody.create(body, MediaType.parse("application/x-www-form-urlencoded")))
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = response.body().string();
      JsonNode node = objectMapper.readTree(responseBody);
      String newToken = node.get("access_token").asText();
      user.setGoogleAccessToken(newToken);
      userRepository.save(user);
      return newToken;
    }
  }

}
