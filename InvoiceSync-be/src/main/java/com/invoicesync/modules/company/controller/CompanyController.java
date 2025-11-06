package com.invoicesync.modules.company.controller;

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

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.company.model.CompanyRequest;
import com.invoicesync.modules.company.model.CompanyResponseDto;
import com.invoicesync.modules.company.service.CompanyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

  private final CompanyService companyService;

  @GetMapping("/user")
  public ResponseEntity<PageResponse<CompanyResponseDto>> findAllCompaniesByUser(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      final Authentication connectedUser
  ) {
    return ResponseEntity.ok(companyService.findAllCompaniesByUser(page, size, connectedUser));
  }

  @GetMapping("/{company-id}")
  public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable("company-id") final Long companyId) {
    return ResponseEntity.ok(companyService.findById(companyId));
  }

  @PostMapping("/save")
  public ResponseEntity<Long> saveCompany(
      @Valid @RequestBody final CompanyRequest request,
      final Authentication connectedUser) {
    return ResponseEntity.ok(companyService.saveCompany(request, connectedUser));
  }

  @PostMapping("/save/{registrationNumber}")
  public ResponseEntity<Long> saveCompanyByRegistrationNumber(
      @PathVariable final String registrationNumber,
      final Authentication connectedUser
  ) {
    return ResponseEntity.ok(companyService.saveCompanyByRegistrationNumber(registrationNumber, connectedUser));
  }

  @PostMapping("/update/{companyId}")
  public ResponseEntity<Company> editCompany(
      @PathVariable final Long companyId,
      @RequestBody final CompanyRequest company) {
    return ResponseEntity.ok(companyService.updateCompany(companyId, company));
  }

  @DeleteMapping("/delete/{companyId}")
  public ResponseEntity<String> deleteCompany(@PathVariable final Long companyId) {
    companyService.deleteCompany(companyId);
    return ResponseEntity.ok("Company with ID " + companyId + " was deleted.");
  }

}
