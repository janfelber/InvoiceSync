package com.invoicesync.receipt.dto.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.invoice.InvoiceStatus;
import com.invoicesync.receipt.Receipt;

public class ReceiptSpecification {

  public static Specification<Receipt> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

  public static Specification<Receipt> withCompanyId(Long companyId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("company").get("id"), companyId);
  }

  public static Specification<Receipt> withStatusAndCompany(final InvoiceStatus status, final Long companyId) {
    return (root, query, cb) -> cb.and(
        cb.equal(root.get("status"), status),
        cb.equal(root.get("company").get("id"), companyId)
    );
  }

}
