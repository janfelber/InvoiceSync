package com.invoicesync.modules.company.mapper;

import org.springframework.stereotype.Service;

import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.company.model.CompanyBillingDto;
import com.invoicesync.modules.company.model.CompanyRequest;
import com.invoicesync.modules.company.model.CompanyResponseDto;
import com.invoicesync.supplier.CompanyLookupResult;

@Service
public class CompanyMapper {

  public Company toCompany(final CompanyRequest request) {
    return Company.builder()
        .id(request.id())
        .name(request.name())
        .city(request.city())
        .street(request.street())
        .streetNumber(request.streetNumber())
        .zip(request.zip())
        .registrationNumber(request.registrationNumber())
        .taxId(request.taxId())
        .vatId(request.vatId())
        .build();
  }

  public CompanyResponseDto toCompanyResponse(final Company company) {
    return CompanyResponseDto.builder()
        .id(company.getId())
        .name(company.getName())
        .city(company.getCity())
        .street(company.getStreet())
        .streetNumber(company.getStreetNumber())
        .zip(company.getZip())
        .registrationNumber(company.getRegistrationNumber())
        .taxId(company.getTaxId())
        .vatId(company.getVatId())
        .cashReceiptNumber(company.getCashReceiptNumber())
        .cardReceiptNumber(company.getCardReceiptNumber())
        .recipientEmail(company.getRecipientEmail())
        .build();
  }

  public CompanyBillingDto toCompanyBilling(CompanyLookupResult partner) {
    return new CompanyBillingDto(
        partner.name(),
        partner.city(),
        partner.street(),
        partner.zip(),
        partner.registrationNumber(),
        partner.taxId(),
        partner.vatId()
    );
  }

}
