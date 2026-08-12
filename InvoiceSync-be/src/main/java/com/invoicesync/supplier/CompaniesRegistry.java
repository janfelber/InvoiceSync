package com.invoicesync.supplier;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class CompaniesRegistry {

  List<CompanyLookupSource> sources;

  private static final Logger log = LoggerFactory.getLogger(CompaniesRegistry.class);

  public CompaniesRegistry(List<CompanyLookupSource> sources) {
    this.sources = sources;
  }

  // TODO: sources are tried in order (SK then CZ) with no country signal to disambiguate -
  //  a registration number could coincidentally match a real company in more than one
  //  country's registry, silently returning the wrong one. Needs a country hint (e.g. VAT ID
  //  prefix extracted directly from the invoice, not from the lookup result) - see [JIRA-IS-255].
  public Optional<CompanyLookupResult> findByRegistrationNumber(final String registrationNumber) {
    for (CompanyLookupSource source : sources) {
      Optional<CompanyLookupResult> result = source.findByRegistrationNumber(registrationNumber);
      if (result.isPresent()) {
        return result;
      }
    }
    return Optional.empty();
  }

}
