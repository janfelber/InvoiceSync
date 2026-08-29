package com.invoicesync.modules.company.service;

import static com.invoicesync.core.enums.PaymentType.CARD;
import static com.invoicesync.modules.company.dto.specification.CompanySpecification.withUserId;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.common.PageableFactory;
import com.invoicesync.core.enums.NumberConfigType;
import com.invoicesync.core.enums.PaymentType;
import com.invoicesync.core.exception.CompanyHasLinkedDocumentsException;
import com.invoicesync.modules.accountingdocument.AccountDocumentNumberService;
import com.invoicesync.modules.auth.security.OwnedCompany;
import com.invoicesync.modules.auth.security.RequiresOwnership;
import com.invoicesync.modules.company.mapper.CompanyMapper;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.company.model.CompanyBillingDto;
import com.invoicesync.modules.company.model.CompanyRequest;
import com.invoicesync.modules.company.model.CompanyResponseDto;
import com.invoicesync.modules.company.repository.CompanyRepository;
import com.invoicesync.supplier.CompaniesRegistry;

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

    final Pageable pageable = PageableFactory.ofDescending(page, size);
    final Page<Company> companies = companyRepository.findAll(withUserId(connectedUser.getName()), pageable);

    return PageResponse.from(companies, companyMapper::toCompanyResponse);
  }

  @Override
  public CompanyBillingDto findByRegistrationNumber(final String registrationNumber,
      final Authentication connectedUser) {

    return companiesRegistry.findByRegistrationNumber(registrationNumber)
        .map(companyMapper::toCompanyBilling)
        .orElseThrow(() ->
            new EntityNotFoundException("No company found with the registration number: " + registrationNumber));
  }

  @Override
  @RequiresOwnership
  public CompanyResponseDto findById(final @OwnedCompany Long companyId) {
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
  @RequiresOwnership
  public Company updateCompany(final @OwnedCompany Long companyId, final CompanyRequest request) {
    final Company oldCompany = companyRepository.findById(companyId).orElse(null);

    if (oldCompany == null) {
      throw new IllegalArgumentException("Company not found");
    }

    if (request.name() != null) {
      oldCompany.setName(request.name());
    }
    if (request.city() != null) {
      oldCompany.setCity(request.city());
    }
    if (request.street() != null) {
      oldCompany.setStreet(request.street());
    }
    if (request.streetNumber() != null) {
      oldCompany.setStreetNumber(request.streetNumber());
    }
    if (request.zip() != null) {
      oldCompany.setZip(request.zip());
    }
    if (request.taxId() != null) {
      oldCompany.setTaxId(request.taxId());
    }
    if (request.vatId() != null) {
      oldCompany.setVatId(request.vatId());
    }
    if (request.registrationNumber() != null) {
      oldCompany.setRegistrationNumber(request.registrationNumber());
    }
    if (request.cashReceiptNumber() != null) {
      oldCompany.setCashReceiptNumber(request.cashReceiptNumber());
    }
    if (request.cardReceiptNumber() != null) {
      oldCompany.setCardReceiptNumber(request.cardReceiptNumber());
    }
    if (request.recipientEmail() != null) {
      oldCompany.setRecipientEmail(request.recipientEmail());
    }
    return companyRepository.save(oldCompany);
  }

  @Override
  @RequiresOwnership
  public Company deleteCompany(final @OwnedCompany Long companyId) {
    final Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new IllegalArgumentException("Company not found"));

    try {
      companyRepository.delete(company);
    } catch (final DataIntegrityViolationException ex) {
      throw new CompanyHasLinkedDocumentsException(
          "Company cannot be deleted because it still has invoices or receipts linked to it");
    }
    return company;
  }

  @Override
  @Transactional
  public String allocateReceiptNumber(final Company company, final PaymentType paymentType,
      final Authentication connectedUser) {

    final NumberConfigType configType = paymentType == CARD
        ? NumberConfigType.RECEIPT_CARD
        : NumberConfigType.RECEIPT_CASH;

    final String currentReceiptNumber = company.getReceiptNumber(paymentType);
    accountDocumentNumberService.checkNumberConfiguration(company, configType);

    company.setReceiptNumber(paymentType, accountDocumentNumberService.incrementDocumentNumber(currentReceiptNumber));
    companyRepository.save(company);
    return currentReceiptNumber;
  }

  @Override
  public Long saveCompanyByRegistrationNumber(final String registrationNumber, final Authentication connectedUser) {
    final Company newCompany = new Company();

    companiesRegistry.findByRegistrationNumber(registrationNumber)
        .ifPresentOrElse(
            foundCompany -> {
              newCompany.setName(foundCompany.name());
              newCompany.setCity(foundCompany.city());
              newCompany.setStreet(foundCompany.street());
              newCompany.setZip(foundCompany.zip());
              newCompany.setRegistrationNumber(registrationNumber);
              newCompany.setTaxId(foundCompany.taxId());
              newCompany.setVatId(foundCompany.vatId());
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