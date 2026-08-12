package com.invoicesync.supplier;

import java.util.Optional;

public interface CompanyLookupSource {

  Optional<CompanyLookupResult> findByRegistrationNumber(String registrationNumber);

}
