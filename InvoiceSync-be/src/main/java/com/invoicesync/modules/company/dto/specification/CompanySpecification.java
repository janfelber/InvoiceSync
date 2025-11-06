package com.invoicesync.modules.company.dto.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.modules.company.model.Company;

public class CompanySpecification {

  public static Specification<Company> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
