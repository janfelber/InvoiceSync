package com.invoicesync.service;

import java.util.List;

import com.invoicesync.dto.CompanyResponseDto;
import com.invoicesync.module.Company;

public interface CompanyService {

  Company addCompany(Company company);

  Company updateCompany(Long companyId, Company company);

  Company deleteCompany(Long companyId);

  List<CompanyResponseDto> getCompaniesByUserId(Long userId);

  CompanyResponseDto getCompanyInfo(Long id);

}