package com.invoicesync.service;

import java.util.List;

import com.invoicesync.dto.CompanyResponseDto;
import com.invoicesync.module.Company;

public interface CompanyService {

  Company addCompany(Company company);

  List<CompanyResponseDto> getCompaniesByUserId(Long userId);

}