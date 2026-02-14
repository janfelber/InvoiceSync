package com.invoicesync.modules.auth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public class ApiKeyUtil {

  private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

  private static final String PREFIX = "mak_";

  private static final SecureRandom RANDOM = new SecureRandom();

  public static String generate() {
    StringBuilder sb = new StringBuilder(PREFIX);
    for (int i = 0; i < 32; i++) {
      sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
    }
    return sb.toString();
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

  public static boolean isApiKey(String token) {
    return token != null && token.startsWith(PREFIX);
  }

}
