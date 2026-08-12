package com.invoicesync.supplier;

import lombok.Builder;

@Builder
public record CompanyLookupResult(

    String registrationNumber,

    String name,

    String taxId,

    String vatId,

    String city,

    String street,

    String zip

) {

}
