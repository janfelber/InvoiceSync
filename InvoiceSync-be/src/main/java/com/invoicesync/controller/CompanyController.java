package com.invoicesync.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.dto.CompanyResponseDto;
import com.invoicesync.module.Company;
import com.invoicesync.service.CompanyService;
import com.invoicesync.user.CurrentUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
public class CompanyController {

  private final CompanyService companyService;
  private final CurrentUserService currentUserService;

  @PostMapping
  @RequestMapping("/add")
  public ResponseEntity<Company> addCompany(@RequestBody final Company company) {
    return ResponseEntity.ok(companyService.addCompany(company));
  }

  @PostMapping("/update/{companyId}")
  public ResponseEntity<Company> editCompany(
      @PathVariable final Long companyId,
      @RequestBody final Company company) {
    return ResponseEntity.ok(companyService.updateCompany(companyId, company));
  }

  @GetMapping("/user")
  public ResponseEntity<List<CompanyResponseDto>> getUserCompanies() {
    return ResponseEntity.ok(companyService.getCompaniesByUserId(getCurrentUserId()));
  }

  @GetMapping("/info/{id}")
  public ResponseEntity<CompanyResponseDto> getCompanyInfo(@PathVariable final Long id) {
    return ResponseEntity.ok(companyService.getCompanyInfo(id));
  }

  @DeleteMapping("/delete/{companyId}")
  public ResponseEntity<Company> deleteCompany(@PathVariable final Long companyId) {
    return ResponseEntity.ok(companyService.deleteCompany(companyId));
  }

  private Long getCurrentUserId() {
    return currentUserService.getCurrentUserId();
  }

}
