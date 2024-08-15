package com.invoicesync.service;

import com.invoicesync.module.Company;

import java.util.List;

public interface CompanyService {
    List<Company> getCompanies();

    List<Company> getCompaniesByUserId(int id);

    Company getCompanyByName(String name);
}
