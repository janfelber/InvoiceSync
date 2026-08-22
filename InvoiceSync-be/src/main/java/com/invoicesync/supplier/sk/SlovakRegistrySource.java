package com.invoicesync.supplier.sk;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import com.invoicesync.supplier.CompanyLookupResult;
import com.invoicesync.supplier.CompanyLookupSource;

//TODO move this to the service and use openAPI from opendata.financnasprava.sk
@Component
public class SlovakRegistrySource implements CompanyLookupSource {

  private List<SlovakPartner> slovakPartners;

  @PostConstruct
  void init() {
    try (InputStream in = getClass().getResourceAsStream("/registry/partners.xml")) {
      if (in == null) {
        this.slovakPartners = List.of();
        return;
      }
      final JAXBContext context = JAXBContext.newInstance(SlovakRootRegistryXml.class);
      final Unmarshaller unmarshaller = context.createUnmarshaller();
      final SlovakRootRegistryXml registryXml = (SlovakRootRegistryXml) unmarshaller.unmarshal(in);
      this.slovakPartners = registryXml.getCompanyList().getItems();
    } catch (Exception e) {
      this.slovakPartners = List.of();
    }
  }

  @Override
  public Optional<CompanyLookupResult> findByRegistrationNumber(final String registrationNumber) {
    return slovakPartners.stream()
        .filter(item -> registrationNumber.equalsIgnoreCase(item.getRegistrationNumber()))
        .findFirst().map(item -> CompanyLookupResult.builder()
            .registrationNumber(item.getRegistrationNumber())
            .name(item.getName())
            .taxId(item.getTaxId())
            .vatId(item.getVatId())
            .city(item.getCity())
            .street(item.getStreet())
            .zip(item.getZip())
            .build()
        );
  }

}
