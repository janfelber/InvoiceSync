package com.invoicesync.invoice;

import org.springframework.data.jpa.domain.Specification;

public class InvoiceSpecification {
  public static Specification<Invoice> withCompanyId(Long companyId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("company").get("id"), companyId);
  }

  public static Specification<Invoice> withUserId(final String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

  public static Specification<Invoice> withStatusAndCompany(final InvoiceStatus status, final Long companyId) {
    System.out.println("status: " + status + ", companyId: " + companyId);
    return (root, query, cb) -> {
      // meno entity / alias
      System.out.println("Entity name: " + root.getModel().getName());

      // všetky polia (atribúty) entity
      root.getModel().getAttributes().forEach(attr -> {
        System.out.println("Attribute: " + attr.getName() + ", type: " + attr.getJavaType());
      });

      // konkrétne podmienky
      return cb.and(
          cb.equal(root.get("status"), status),
          cb.equal(root.get("company").get("id"), companyId)
      );
    };
  }
}
