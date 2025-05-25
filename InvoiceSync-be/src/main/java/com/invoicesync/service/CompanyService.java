package com.invoicesync.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.common.PageResponse;
import com.invoicesync.dto.CompanyResponseDto;
import com.invoicesync.module.Company;

public interface CompanyService {

  PageResponse<CompanyResponseDto> findAllCompaniesByUser(int size, int page, Authentication connectedUser);

  CompanyResponseDto findById(Long companyId);

  Long saveCompany(CompanyRequest request, Authentication connectedUser);

  Company updateCompany(Long companyId, CompanyRequest company);

  Company deleteCompany(Long companyId);

}