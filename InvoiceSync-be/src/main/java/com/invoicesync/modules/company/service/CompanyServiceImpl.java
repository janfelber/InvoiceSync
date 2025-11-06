package com.invoicesync.modules.company.service;

import static com.invoicesync.modules.company.dto.specification.CompanySpecification.withUserId;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.enums.NumberConfigType;
import com.invoicesync.modules.accountingdocument.AccountDocumentNumberService;
import com.invoicesync.modules.company.mapper.CompanyMapper;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.company.model.CompanyRequest;
import com.invoicesync.modules.company.model.CompanyResponseDto;
import com.invoicesync.modules.company.repository.CompanyRepository;
import com.invoicesync.partner.CompaniesRegistry;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final AccountDocumentNumberService accountDocumentNumberService;

  private final CompanyRepository companyRepository;

  private final CompaniesRegistry companiesRegistry;

  private final CompanyMapper companyMapper;

  // public static boolean hasFeature(final Authentication authentication, final Feature feature) {
  //   final Jwt jwt = (Jwt) authentication.getPrincipal();
  //   final List<String> roles = (List<String>) ((Map<String, Object>) jwt.getClaim("realm_access")).get("roles");
  //   return roles.contains(feature.getRoleName());
  // }

  @Override
  public PageResponse<CompanyResponseDto> findAllCompaniesByUser(final int page, int size,
      final Authentication connectedUser) {

    final Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    final Page<Company> companies = companyRepository.findAll(withUserId(connectedUser.getName()), pageable);

    final List<CompanyResponseDto> companyResponse = companies.stream()
        .map(companyMapper::toCompanyResponse)
        .toList();

    return new PageResponse<>(
        companyResponse,
        companies.getNumber(),
        companies.getSize(),
        companies.getTotalElements(),
        companies.getTotalPages(),
        companies.isFirst(),
        companies.isLast()
    );
  }

  @Override
  public CompanyResponseDto findById(final Long companyId) {
    return companyRepository.findById(companyId)
        .map(companyMapper::toCompanyResponse)
        .orElseThrow(() -> new EntityNotFoundException("No company found with the ID: " + companyId));
  }

  @Override
  public Long saveCompany(final CompanyRequest request, final Authentication connectedUser) {
    final Company company = companyMapper.toCompany(request);
    return companyRepository.save(company).getId();
  }

  @Override
  public Company updateCompany(final Long companyId, final CompanyRequest request) {
    final Company oldCompany = companyRepository.findById(companyId).orElse(null);

    if (oldCompany == null) {
      throw new IllegalArgumentException("Company not found");
    }

    if (request.name() != null) oldCompany.setName(request.name());
    if (request.city() != null) oldCompany.setCity(request.city());
    if (request.street() != null) oldCompany.setStreet(request.street());
    if (request.streetNumber() != null) oldCompany.setStreetNumber(request.streetNumber());
    if (request.zip() != null) oldCompany.setZip(request.zip());
    if (request.taxId() != null) oldCompany.setTaxId(request.taxId());
    if (request.vatId() != null) oldCompany.setVatId(request.vatId());
    if (request.registrationNumber() != null) oldCompany.setRegistrationNumber(request.registrationNumber());
    if (request.cashReceiptNumber() != null) oldCompany.setCashReceiptNumber(request.cashReceiptNumber());
    if (request.cardReceiptNumber() != null) oldCompany.setCardReceiptNumber(request.cardReceiptNumber());
    return companyRepository.save(oldCompany);
  }

  @Override
  public Company deleteCompany(final Long companyId) {
    final Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new IllegalArgumentException("Company not found"));

    companyRepository.delete(company);
    return company;
  }

  @Override
  @Transactional
  public String getReceiptNumber(final Long companyId, final boolean paidByCard, final Authentication connectedUser) {
    final Company company = companyRepository.findById(companyId).orElse(null);

    final NumberConfigType configType = paidByCard
        ? NumberConfigType.RECEIPT_CARD
        : NumberConfigType.RECEIPT_CASH;

    final String currentDocumentNumber = paidByCard
        ? company.getCardReceiptNumber()
        : company.getCashReceiptNumber();

    accountDocumentNumberService.checkNumberConfiguration(company, configType);

    final String next = accountDocumentNumberService.incrementDocumentNumber(currentDocumentNumber);

    if (paidByCard) {
      company.setCardReceiptNumber(next);
    } else {
      company.setCashReceiptNumber(next);
    }

    companyRepository.save(company);
    return currentDocumentNumber;
  }

  @Override
  public Long saveCompanyByRegistrationNumber(final String registrationNumber, final Authentication connectedUser) {
    final Company newCompany = new Company();

    companiesRegistry.findByIco(registrationNumber)
        .ifPresentOrElse(
            foundCompany -> {
              newCompany.setName(foundCompany.getName());
              newCompany.setCity(foundCompany.getCity());
              newCompany.setStreet(foundCompany.getStreet());
              newCompany.setZip(foundCompany.getZip());
              newCompany.setRegistrationNumber(registrationNumber);
              newCompany.setTaxId(foundCompany.getTaxId());
              newCompany.setVatId(foundCompany.getVatId());
              newCompany.setCardReceiptNumber(null);
              newCompany.setCashReceiptNumber(null);
              companyRepository.save(newCompany);
            },
            () -> {
              throw new IllegalArgumentException("Firma s IČO " + registrationNumber + " nebola nájdená v registri.");
            }
        );

    return newCompany.getId();
  }

}