package com.invoicesync.chartaccount;

import org.springframework.stereotype.Service;

import com.invoicesync.chartaccount.dto.ChartAccountRequest;
import com.invoicesync.company.Company;
import com.invoicesync.company.CompanyRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChartAccountMapper {

  private final CompanyRepository companyRepository;

  public ChartAccount toChartAccount(final ChartAccountRequest request) {
    System.out.println("Mapper Request: " + request);
    final Company company = companyRepository.findById(request.companyId()).orElseThrow();

    return ChartAccount.builder()
        .classId("99")
        .company(company)
        .className("Vlastné účty")
        .accountId(request.accountId())
        .accountName(request.accountName())
        .category(AccountUtils.getCategoryIdFromAccount(request.accountId()))
        .categoryName(AccountUtils.getCategoryIdFromAccount(request.accountId()))
        .isEditable(true)
        .build();
  }

}
