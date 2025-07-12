package com.invoicesync.invoice;

import org.springframework.data.jpa.domain.Specification;

public class InvoiceSpecification {
  public static Specification<Invoice> withCompanyId(Long companyId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("company").get("id"), companyId);
  }
}
