package com.example.invoicesync.controller;

import com.example.invoicesync.module.Company;
import com.example.invoicesync.service.CompanyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    //get all companies
    @GetMapping("/company")
    public List<Map<String, String>> getCompanies() {
        List <Company> companies = companyService.getCompanies();
        List<Map<String, String>> result = new ArrayList<>();
        for (Company company : companies) {
            Map<String, String> map = new HashMap<>();
            map.put("company_id", String.valueOf(company.getId()));
            map.put("name", company.getName());
            map.put("user_name", company.getUser().getUsername());
            result.add(map);
        }
        return result;
    }

    //get company for specific user
    @GetMapping("/company/user/{id}")
    public List<Map<String, String>> getCompaniesByUserId(@PathVariable int id) {
        List<Company> companies = companyService.getCompaniesByUserId(id);
        List<Map<String, String>> result = new ArrayList<>();
        for (Company company : companies) {
            Map<String, String> map = new HashMap<>();
            map.put("company_id", String.valueOf(company.getId()));
            map.put("name", company.getName());
            result.add(map);

        }
        return result;
    }

    //get id of company by name
    @GetMapping("/company/{name}")
    public String getCompanyIdByName(@PathVariable String name) {
        Company company = companyService.getCompanyByName(name);
        return String.valueOf(company.getId());
    }

}
