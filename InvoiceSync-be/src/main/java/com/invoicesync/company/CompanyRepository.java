package com.invoicesync.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

  // List<Company> findByUserId(Long id);

  // boolean existsByIdAndUserId(Long companyId, Long userId);

}
