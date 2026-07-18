package com.invoicesync.shared;

import lombok.Builder;

@Builder
public record OrganizationDto(

    //null for supplier, since the system is not storing suppliers
    Long id,

    String name,

    String street,

    String streetNumber,

    String city,

    String postalCode,

    // registrationNumber, well-known is Slovak as IČO
    String registrationNumber,

    // tax identification number, used for tax purposes in the country, known in Slovak as DIČ
    String taxId,

    // value-added tax identification number, usually taxId prefixed with a country code (e.g. "SK2021234567"), known in Slovak as IČ DPH
    String vatId,

    // email address used for sending invoices and other communications; null for supplier, filled in for company
    String targetCompanyEmail

) {

}
