package com.invoicesync.chartaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.invoicesync.company.Company;

@Repository
public interface ChartAccountRepository
    extends JpaRepository<ChartAccount, Long>, JpaSpecificationExecutor<ChartAccount> {

  boolean existsByCompanyAndAccountId(Company company, String accountId);

}
