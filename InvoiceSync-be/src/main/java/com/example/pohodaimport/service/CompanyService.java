package com.example.pohodaimport.service;

import com.example.pohodaimport.module.Company;

import java.util.List;

public interface CompanyService {
    List<Company> getCompanies();

    List<Company> getCompaniesByUserId(int id);

    Company getCompanyByName(String name);
}
