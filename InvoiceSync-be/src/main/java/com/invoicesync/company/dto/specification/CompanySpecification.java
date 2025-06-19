package com.invoicesync.company.dto.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.company.Company;

public class CompanySpecification {

  public static Specification<Company> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
