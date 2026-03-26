package com.invoicesync.modules.company.model;

import lombok.Builder;

@Builder
public record CompanyBillingDto(

    String name,

    String city,

    String street,

    String zip,

    String registrationNumber,

    String taxId,

    String vatId
) {

}
