package com.invoicesync.modules.company.model;

import lombok.Builder;

@Builder
public record CompanyBillingDto(

    String name,

    String address,

    String registrationNumber,

    String taxId,

    String vatId
) {

}
