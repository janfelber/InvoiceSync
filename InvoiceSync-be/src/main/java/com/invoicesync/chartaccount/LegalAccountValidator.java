package com.invoicesync.chartaccount;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
public class LegalAccountValidator {

  private Set<String> legalAccountIds;

  @PostConstruct
  public void init() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      InputStream is = getClass().getResourceAsStream("/legal-accounts/legal-accounts.json");

      List<Map<String, String>> accounts = mapper.readValue(is, new TypeReference<>() {});

      legalAccountIds = accounts.stream()
          .map(acc -> acc.get("accountId"))
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());

    } catch (Exception e) {
      throw new RuntimeException("Nepodarilo sa načítať legal-accounts.json", e);
    }
  }

  public boolean isLegalAccount(final String accountId) {
    return legalAccountIds.contains(accountId);
  }

}
