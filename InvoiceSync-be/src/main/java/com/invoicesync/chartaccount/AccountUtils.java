package com.invoicesync.chartaccount;

public class AccountUtils {
  public static String getCategoryIdFromAccount(final String accountId) {
    if (accountId == null || accountId.isEmpty()) {
      return "unknown";
    }
    return accountId.length() >= 3 ? accountId.substring(0, 3) : accountId.substring(0, 2);
  }
}
