package com.invoicesync.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.module.Receipt;

public class ReceiptSpecification {

  public static Specification<Receipt> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

  public static Specification<Receipt> withCompanyId(Long companyId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("company").get("id"), companyId);
  }



}
