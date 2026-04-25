package com.invoicesync.modules.company.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.enums.PaymentType;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.company.model.CompanyBillingDto;
import com.invoicesync.modules.company.model.CompanyRequest;
import com.invoicesync.modules.company.model.CompanyResponseDto;

public interface CompanyService {

  /**
   * Retrieves a paginated list of companies associated with the connected user.
   *
   * @param size number of companies per page
   * @param page page number (0-based)
   * @param connectedUser currently authenticated user
   * @return paginated response of companies
   */
  PageResponse<CompanyResponseDto> findAllCompaniesByUser(int size, int page, Authentication connectedUser);

  CompanyBillingDto findByRegistrationNumber(String registrationNumber, Authentication connectedUser);

  /**
   * Finds a company by its unique ID.
   *
   * @param companyId ID of the company
   * @return company data as a response DTO
   */
  CompanyResponseDto findById(Long companyId);

  /**
   * Creates a new company for the connected user.
   *
   * @param request company creation request data
   * @param connectedUser currently authenticated user
   * @return ID of the newly created company
   */
  Long saveCompany(CompanyRequest request, Authentication connectedUser);

  /**
   * Creates a new company for the connected user.
   *
   * @param registrationNumber id that holds information about company
   * @param connectedUser currently authenticated user
   * @return ID of the newly created company
   */
  Long saveCompanyByRegistrationNumber(String registrationNumber, Authentication connectedUser);

  /**
   * Updates an existing company.
   *
   * @param companyId ID of the company to update
   * @param company updated company data
   * @return updated Company entity
   */
  Company updateCompany(Long companyId, CompanyRequest company);

  /**
   * Deletes a company by its ID.
   *
   * @param companyId ID of the company to delete
   * @return deleted Company entity
   */
  Company deleteCompany(Long companyId);

  String allocateReceiptNumber(Company company, PaymentType paymentType, Authentication connectedUser);

}