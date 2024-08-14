package com.example.invoicesync.service;

import com.example.invoicesync.module.Company;

import java.util.List;

public interface CompanyService {
    List<Company> getCompanies();

    List<Company> getCompaniesByUserId(int id);

    Company getCompanyByName(String name);
}
