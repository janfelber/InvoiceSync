package com.invoicesync.modules.auth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public class RefreshTokenUtil {

  private static final SecureRandom RANDOM = new SecureRandom();

  public static String generate() {
    byte[] bytes = new byte[64];
    RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  public static String hash(String plainKey) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] bytes = digest.digest(plainKey.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(bytes);
    } catch (Exception e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }

}
