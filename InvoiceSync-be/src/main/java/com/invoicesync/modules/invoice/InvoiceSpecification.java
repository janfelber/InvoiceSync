package com.invoicesync.modules.invoice;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.core.enums.InvoiceStatus;
import com.invoicesync.modules.invoice.model.Invoice;

public class InvoiceSpecification {
  public static Specification<Invoice> withCompanyId(Long companyId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("company").get("id"), companyId);
  }

  public static Specification<Invoice> withUserId(final String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

  public static Specification<Invoice> withStatusAndCompany(final InvoiceStatus status, final Long companyId) {
    return (root, query, cb) -> {
      root.getModel().getAttributes().forEach(attr -> {
      });
      return cb.and(
          cb.equal(root.get("status"), status),
          cb.equal(root.get("company").get("id"), companyId)
      );
    };
  }
}
