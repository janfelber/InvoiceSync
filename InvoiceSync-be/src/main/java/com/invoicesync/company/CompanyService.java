package com.invoicesync.company;

import org.springframework.security.core.Authentication;

import com.invoicesync.company.dto.CompanyRequest;
import com.invoicesync.company.dto.CompanyResponseDto;
import com.invoicesync.shared.common.PageResponse;

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

  String getReceiptNumber(Long companyId, boolean paidByCard, Authentication connectedUser);

}