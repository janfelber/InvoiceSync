package com.invoicesync.modules.company.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.company.model.CompanyBillingDto;
import com.invoicesync.modules.company.model.CompanyRequest;
import com.invoicesync.modules.company.model.CompanyResponseDto;
import com.invoicesync.modules.company.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Api.COMPANY)
public class CompanyController {

  private final CompanyService companyService;

  @GetMapping(Api.COMPANY_GET_BY_REGISTRATION_NUMBER)
  public ResponseEntity<CompanyBillingDto> getCompanyByRegistrationNumber(
      @PathVariable final String registrationNumber, final Authentication connectedUser) {
    return ResponseEntity.ok(companyService.findByRegistrationNumber(registrationNumber, connectedUser));
  }

  @GetMapping(Api.GET_BY_USER)
  public ResponseEntity<PageResponse<CompanyResponseDto>> findAllCompaniesByUser(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      final Authentication connectedUser
  ) {
    return ResponseEntity.ok(companyService.findAllCompaniesByUser(page, size, connectedUser));
  }

  @GetMapping(Api.COMPANY_GET_BY_ID)
  public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable("company-id") final Long companyId) {
    return ResponseEntity.ok(companyService.findById(companyId));
  }

  @PostMapping(Api.SAVE)
  public ResponseEntity<Long> saveCompany(
      @Valid @RequestBody final CompanyRequest request,
      final Authentication connectedUser) {
    return ResponseEntity.ok(companyService.saveCompany(request, connectedUser));
  }

  @PostMapping(Api.COMPANY_SAVE_BY_REGISTRATION_NUMBER)
  public ResponseEntity<Long> saveCompanyByRegistrationNumber(
      @PathVariable final String registrationNumber,
      final Authentication connectedUser
  ) {
    return ResponseEntity.ok(companyService.saveCompanyByRegistrationNumber(registrationNumber, connectedUser));
  }

  @PostMapping(Api.COMPANY_UPDATE)
  public ResponseEntity<Company> editCompany(
      @PathVariable final Long companyId,
      @RequestBody final CompanyRequest company) {
    return ResponseEntity.ok(companyService.updateCompany(companyId, company));
  }

  @DeleteMapping(Api.COMPANY_DELETE)
  public ResponseEntity<String> deleteCompany(@PathVariable final Long companyId) {
    companyService.deleteCompany(companyId);
    return ResponseEntity.ok("Company with ID " + companyId + " was deleted.");
  }

}
