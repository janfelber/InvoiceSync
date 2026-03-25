package com.invoicesync.integration.google.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;

@Service
public class GoogleIntegrationServiceImpl implements GoogleIntegrationService {

  @Value("${google.client-id}")
  private String clientId;

  @Value("${google.client-secret}")
  private String clientSecret;

  @Value("${google.redirect-uri}")
  private String redirectUri;

  private final UserRepository userRepository;

  public GoogleIntegrationServiceImpl(final UserRepository userRepository) {
    this.userRepository = userRepository;
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
        "&state=" + userId.toString(); // passes user id to callback
  }

  @Override
  public void handleCallback(String code, UUID userId) throws IOException {
    // Change this to use the Google API client library
    GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
        new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        clientId,
        clientSecret,
        code,
        redirectUri
    ).execute();

    // Get the user's email address
    String googleEmail = tokenResponse.parseIdToken().getPayload().getEmail();

    // Save the user's Google access token and refresh token
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setGoogleAccessToken(tokenResponse.getAccessToken());
    user.setGoogleRefreshToken(tokenResponse.getRefreshToken());
    user.setGoogleEmail(googleEmail);
    user.setGoogleConnected(true);

    userRepository.save(user);
  }

  public void disconnect(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setGoogleAccessToken(null);
    user.setGoogleRefreshToken(null);
    user.setGoogleEmail(null);
    user.setGoogleConnected(false);

    userRepository.save(user);
  }

}