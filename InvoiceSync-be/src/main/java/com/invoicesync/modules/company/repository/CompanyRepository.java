package com.invoicesync.modules.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.invoicesync.modules.company.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

  // List<Company> findByUserId(Long id);

  // boolean existsByIdAndUserId(Long companyId, Long userId);

}
