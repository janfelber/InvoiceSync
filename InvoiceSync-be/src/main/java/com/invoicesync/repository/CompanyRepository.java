package com.invoicesync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.module.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

      List<Company> findByUserId(Long id);
}
