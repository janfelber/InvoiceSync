package com.invoicesync.supplier.sk;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import com.invoicesync.supplier.CompanyLookupResult;
import com.invoicesync.supplier.CompanyLookupSource;

import lombok.extern.slf4j.Slf4j;

// The source file (~230k entries, ~70MB) is too large to unmarshal into a full JAXB object
// tree on memory-constrained hosts (blew the heap on Render). We stream it item-by-item with
// StAX instead and keep only the compact lookup projection, discarding each SlovakPartner as
// we go.
//TODO move this to the service and use openAPI from opendata.financnasprava.sk
@Slf4j
@Component
public class SlovakRegistrySource implements CompanyLookupSource {

  private static final String ITEM_ELEMENT = "ITEM";

  private Map<String, CompanyLookupResult> partnersByRegistrationNumber = Map.of();

  @PostConstruct
  void init() {
    try (InputStream in = getClass().getResourceAsStream("/registry/partners.xml")) {
      if (in != null) {
        partnersByRegistrationNumber = streamPartners(in);
      }
    } catch (Throwable e) {
      log.warn("Failed to load Slovak company registry, lookups will return empty", e);
      partnersByRegistrationNumber = Map.of();
    }
  }

  private Map<String, CompanyLookupResult> streamPartners(final InputStream in) throws Exception {
    final XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
    final Unmarshaller unmarshaller = JAXBContext.newInstance(SlovakPartner.class).createUnmarshaller();
    final Map<String, CompanyLookupResult> result = new HashMap<>();
    try {
      while (reader.hasNext()) {
        if (reader.next() == XMLStreamConstants.START_ELEMENT && ITEM_ELEMENT.equals(reader.getLocalName())) {
          final JAXBElement<SlovakPartner> element = unmarshaller.unmarshal(reader, SlovakPartner.class);
          final SlovakPartner partner = element.getValue();
          if (partner.getRegistrationNumber() != null) {
            result.put(partner.getRegistrationNumber().toUpperCase(), toLookupResult(partner));
          }
        }
      }
    } finally {
      reader.close();
    }
    return result;
  }

  private CompanyLookupResult toLookupResult(final SlovakPartner partner) {
    return CompanyLookupResult.builder()
        .registrationNumber(partner.getRegistrationNumber())
        .name(partner.getName())
        .taxId(partner.getTaxId())
        .vatId(partner.getVatId())
        .city(partner.getCity())
        .street(partner.getStreet())
        .zip(partner.getZip())
        .build();
  }

  @Override
  public Optional<CompanyLookupResult> findByRegistrationNumber(final String registrationNumber) {
    if (registrationNumber == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(partnersByRegistrationNumber.get(registrationNumber.toUpperCase()));
  }

}
