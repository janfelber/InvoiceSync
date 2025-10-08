package com.invoicesync.company;

import static com.invoicesync.company.dto.specification.CompanySpecification.withUserId;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.invoicesync.company.dto.CompanyRequest;
import com.invoicesync.company.dto.CompanyResponseDto;
import com.invoicesync.feature.Feature;
import com.invoicesync.partner.CompaniesRegistry;
import com.invoicesync.shared.common.PageResponse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final CompanyRepository companyRepository;

  private final CompaniesRegistry companiesRegistry;

  private final CompanyMapper companyMapper;

  public static boolean hasFeature(final Authentication authentication, final Feature feature) {
    final Jwt jwt = (Jwt) authentication.getPrincipal();
    final List<String> roles = (List<String>) ((Map<String, Object>) jwt.getClaim("realm_access")).get("roles");
    return roles.contains(feature.getRoleName());
  }

  @Override
  public PageResponse<CompanyResponseDto> findAllCompaniesByUser(final int page, int size,
      final Authentication connectedUser) {
    // final UserDemo user = ((UserDemo) connectedUser.getPrincipal());
    final Jwt jwt = (Jwt) connectedUser.getPrincipal();
    final List<String> roles = (List<String>) ((Map<String, Object>) jwt.getClaim("realm_access")).get("roles");
    System.out.println("Roles: " + roles);

    if (size < 1) {
      size = 1;
    }
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
    // final UserDemo user = ((UserDemo) connectedUser.getPrincipal());
    final Company company = companyMapper.toCompany(request);
    // company.setUser(user);
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

    final String current;

    if (paidByCard) {
      current = company.getCardReceiptNumber();
      company.setCardReceiptNumber(increment(current));
    } else {
      current = company.getCashReceiptNumber();
      company.setCashReceiptNumber(increment(current));
    }
    companyRepository.save(company);
    return current;
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

  private String increment(final String number) {
    // nájde všetky číslice na konci reťazca
    final Matcher matcher = Pattern.compile("(\\d+)$").matcher(number);
    if (matcher.find()) {
      final String digits = matcher.group(1);
      final int length = digits.length();
      final int next = Integer.parseInt(digits) + 1;
      final String prefix = number.substring(0, matcher.start(1));
      return prefix + String.format("%0" + length + "d", next);
    } else {
      return number + "1";
    }
  }

}