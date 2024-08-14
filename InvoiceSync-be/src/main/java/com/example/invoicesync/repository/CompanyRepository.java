package com.example.invoicesync.repository;

import com.example.invoicesync.module.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer>{
    List<Company> findByUserId(int id);

    Company findByName(String name);
}
