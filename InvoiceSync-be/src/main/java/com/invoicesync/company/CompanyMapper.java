package com.invoicesync.company;

import org.springframework.stereotype.Service;

import com.invoicesync.company.dto.CompanyRequest;
import com.invoicesync.company.dto.CompanyResponseDto;
import com.invoicesync.company.dto.CompanyResponseDtoMobile;

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

  public CompanyResponseDtoMobile toCompanyMobileResponse(final Company company) {
    return CompanyResponseDtoMobile.builder()
        .id(company.getId())
        .name(company.getName())
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
        .build();
  }

}
