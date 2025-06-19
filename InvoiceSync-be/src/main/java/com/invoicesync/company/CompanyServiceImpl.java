package com.invoicesync.company;

import static com.invoicesync.company.dto.specification.CompanySpecification.withUserId;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.company.dto.CompanyRequest;
import com.invoicesync.company.dto.CompanyResponseDto;
import com.invoicesync.shared.common.PageResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final CompanyRepository companyRepository;

  private final CompanyMapper companyMapper;

  @Override
  public PageResponse<CompanyResponseDto> findAllCompaniesByUser(final int page, int size,
      final Authentication connectedUser) {
    // final UserDemo user = ((UserDemo) connectedUser.getPrincipal());
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

    oldCompany.setName(request.name());
    oldCompany.setCity(request.city());
    oldCompany.setStreet(request.street());
    oldCompany.setStreetNumber(request.streetNumber());
    oldCompany.setZip(request.zip());
    oldCompany.setTaxId(request.taxId());
    oldCompany.setVatId(request.vatId());
    oldCompany.setRegistrationNumber(request.registrationNumber());
    return companyRepository.save(oldCompany);
  }

  @Override
  public Company deleteCompany(final Long companyId) {
    final Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new IllegalArgumentException("Company not found"));

    companyRepository.delete(company);
    return company;
  }

}