package com.invoicesync.repository;

import com.invoicesync.module.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer>{
    List<Company> findByUserId(int id);

    Company findByName(String name);
}
