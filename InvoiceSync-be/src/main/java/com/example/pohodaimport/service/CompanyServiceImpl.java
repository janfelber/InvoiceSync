package com.example.pohodaimport.service;

import com.example.pohodaimport.module.Company;
import com.example.pohodaimport.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService{

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public List<Company> getCompaniesByUserId(int id) {
        return companyRepository.findByUserId(id);
    }

    @Override
    public Company getCompanyByName(String name) {
        return companyRepository.findByName(name);
    }
}
