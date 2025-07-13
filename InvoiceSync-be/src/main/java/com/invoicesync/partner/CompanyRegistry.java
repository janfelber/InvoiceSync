package com.invoicesync.partner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class CompanyRegistry {

  private List<PartnerItem> partners;

  private static final Logger log = LoggerFactory.getLogger(CompanyRegistry.class);
  private final Map<String, PartnerItem> byIco = new HashMap<>();

  @PostConstruct
  void init() {
    try (InputStream in = getClass().getResourceAsStream("/registry/partners.xml")) {
      final JAXBContext context = JAXBContext.newInstance(RootRegistryXml.class);
      final Unmarshaller unmarshaller = context.createUnmarshaller();
      final RootRegistryXml registryXml = (RootRegistryXml) unmarshaller.unmarshal(in);

      this.partners = registryXml.getCompanyList().getItems();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load subject registry", e);
    }
  }

  public Optional<PartnerItem> findByIco(final String ico) {
    return partners.stream()
        .filter(item -> ico.equalsIgnoreCase(item.getRegistrationNumber()))
        .findFirst();
  }
}
