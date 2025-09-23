package com.invoicesync.company;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.invoicesync.company.dto.CompanyRequest;
import com.invoicesync.company.dto.CompanyResponseDto;
import com.invoicesync.company.dto.CompanyResponseDtoMobile;
import com.invoicesync.shared.common.PageResponse;

public interface CompanyService {

  PageResponse<CompanyResponseDto> findAllCompaniesByUser(int size, int page, Authentication connectedUser);

  List<CompanyResponseDtoMobile> findAllCompaniesByUser(Authentication connectedUser);

  CompanyResponseDto findById(Long companyId);

  Long saveCompany(CompanyRequest request, Authentication connectedUser);

  Company updateCompany(Long companyId, CompanyRequest company);

  Company deleteCompany(Long companyId);

}