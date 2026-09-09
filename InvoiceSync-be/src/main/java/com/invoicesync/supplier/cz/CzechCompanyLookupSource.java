package com.invoicesync.supplier.cz;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.invoicesync.supplier.CompanyLookupResult;
import com.invoicesync.supplier.CompanyLookupSource;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CzechCompanyLookupSource implements CompanyLookupSource {

  @Qualifier("aresWebClient")
  private final WebClient aresWebClient;

  public CzechCompanyLookupSource(@Qualifier("aresWebClient") WebClient aresWebClient) {
    this.aresWebClient = aresWebClient;
  }

  @Override
  public Optional<CompanyLookupResult> findByRegistrationNumber(final String registrationNumber) {

    try {
      AresResponseDto response = aresWebClient.get()
          .uri("/{ico}", registrationNumber)
          .retrieve()
          .bodyToMono(AresResponseDto.class)
          .block();

      if (response != null) {
        return Optional.of(CompanyLookupResult.builder()
            .registrationNumber(response.registrationNumber())
            .name(response.name())
            .taxId(response.taxId())
            .vatId(response.vatId())
            .city(response.registeredOffice().city())
            .street(response.registeredOffice().street())
            .zip(response.registeredOffice().postalCode())
            .build());
      }
    } catch (Exception e) {
      log.warn("[EXTERNAL] Failed to fetch company info from ARES for registrationNumber={}",
          registrationNumber, e);
    }

    return Optional.empty();
  }

}
