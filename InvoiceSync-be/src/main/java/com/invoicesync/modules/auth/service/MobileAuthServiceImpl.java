package com.invoicesync.modules.auth.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.invoicesync.core.audit.CustomUserDetails;
import com.invoicesync.modules.auth.model.MobileApiKey;
import com.invoicesync.modules.auth.model.MobileQrResponse;
import com.invoicesync.modules.auth.model.MobileSession;
import com.invoicesync.modules.auth.model.MobileVerifyRequest;
import com.invoicesync.modules.auth.model.MobileVerifyResponse;
import com.invoicesync.modules.auth.repository.MobileApiKeyRepository;
import com.invoicesync.modules.auth.repository.MobileSessionRepository;
import com.invoicesync.modules.auth.security.ApiKeyUtil;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;

@Service
public class MobileAuthServiceImpl implements MobileAuthService {

  private final MobileSessionRepository mobileSessionRepository;

  private final MobileApiKeyRepository mobileApiKeyRepository;

  private final UserRepository userRepository;

  private final ObjectMapper objectMapper;

  public MobileAuthServiceImpl(final MobileSessionRepository mobileSessionRepository,
      final MobileApiKeyRepository mobileApiKeyRepository, final UserRepository userRepository,
      final ObjectMapper objectMapper
  ) {
    this.mobileSessionRepository = mobileSessionRepository;
    this.mobileApiKeyRepository = mobileApiKeyRepository;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public MobileQrResponse generateQrSession(final Authentication connectedUser) {

    CustomUserDetails details = (CustomUserDetails) connectedUser.getPrincipal();
    UUID userId = details.getUser().getId();

    // 2. Create a random session token
    String sessionToken = UUID.randomUUID().toString();

    // 3. Save a MobileSession (expires in 5 min)
    MobileSession session = MobileSession.builder()
        .sessionToken(sessionToken)
        .userId(userId)
        .expiresAt(LocalDateTime.now().plusMinutes(5))
        .used(false)
        .build();
    mobileSessionRepository.save(session);

    // 4. Build the QR payload
    String qrData = null;
    try {
      qrData = objectMapper.writeValueAsString(Map.of(
          "type", "auth",
          "token", sessionToken,
          "baseUrl", "https://api.invoicesync.sk"
      ));
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }

    // 5. Return it
    return new MobileQrResponse(qrData, 300);
  }

  @Override
  public MobileVerifyResponse verifyAndCreateApiKey(final MobileVerifyRequest request) {
    // Find session by token
    MobileSession session = mobileSessionRepository.findBySessionToken(request.getSessionToken())
        .orElseThrow(() -> new RuntimeException("Invalid session token"));

    // Check if session is valid
    if (session.isUsed() || session.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new RuntimeException("Session token is expired or already used");
    }

    // Mark session as used
    session.setUsed(true);
    mobileSessionRepository.save(session);

    String plainApiKey = ApiKeyUtil.generate();
    String hashedApiKey = ApiKeyUtil.hash(plainApiKey);

    MobileApiKey apiKey = MobileApiKey.builder()
        .apiKeyHash(hashedApiKey)
        .userId(session.getUserId())
        .deviceName(request.getDeviceName())
        .build();
    mobileApiKeyRepository.save(apiKey);

    return new MobileVerifyResponse(plainApiKey);
  }

  @Override
  public User validateApiKey(final String plainApiKey) {
    String hash = ApiKeyUtil.hash(plainApiKey);
    MobileApiKey apiKey = mobileApiKeyRepository
        .findByApiKeyHashAndActiveTrue(hash)
        .orElse(null);

    if (apiKey == null) {
      return null;
    }

    // Update last used timestamp
    apiKey.setLastUsedAt(LocalDateTime.now());
    mobileApiKeyRepository.save(apiKey);

    // Return the user
    return userRepository.findById(apiKey.getUserId()).orElse(null);
  }

}
