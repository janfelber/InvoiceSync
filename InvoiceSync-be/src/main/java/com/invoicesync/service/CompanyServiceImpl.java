package com.invoicesync.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.invoicesync.dto.CompanyResponseDto;
import com.invoicesync.module.Company;
import com.invoicesync.repository.CompanyRepository;
import com.invoicesync.user.UserDemo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final CompanyRepository companyRepository;

  @Override
  public Company addCompany(final Company company) {
    if (company.getName() == null || company.getName().isEmpty()) {
      throw new IllegalArgumentException("Company name cannot be null or empty");
    }
    company.setUser((UserDemo) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return companyRepository.save(company);
  }

  @Override
  public List<CompanyResponseDto> getCompaniesByUserId(final Long userId) {
    return companyRepository.findByUserId(userId)
        .stream()
        .map(company -> new CompanyResponseDto(
            company.getId(),
            company.getName(),
            company.getCity(),
            company.getStreet(),
            company.getStreetNumber(),
            company.getZip(),
            company.getRegistrationNumber(),
            company.getTaxId(),
            company.getVatId()
        ))
        .collect(Collectors.toList());
  }

  @Override
  public CompanyResponseDto getCompanyInfo(final Long id) {
    return companyRepository.findById(id)
        .map(company -> new CompanyResponseDto(
            company.getId(),
            company.getName(),
            company.getCity(),
            company.getStreet(),
            company.getStreetNumber(),
            company.getZip(),
            company.getRegistrationNumber(),
            company.getTaxId(),
            company.getVatId()
        ))
        .orElseThrow(() -> new IllegalArgumentException("Company with id " + id + " not found"));
  }

}