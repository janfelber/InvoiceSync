package com.invoicesync.postingaccount.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.postingaccount.documents.cash.CashDocumentAccount;
import com.invoicesync.postingaccount.documents.internal.InternalDocumentAccount;

public class PostingAccountSpecification {

  public static Specification<InternalDocumentAccount> internalDocumentsCompanyId(final Long companyId) {
    return (root, query, cb) -> cb.or(
        cb.equal(root.get("company").get("id"), companyId),
        cb.isNull(root.get("company"))
    );
  }

  public static Specification<CashDocumentAccount> cashDocumentsCompanyId(final Long companyId) {
    return (root, query, cb) -> cb.or(
        cb.equal(root.get("company").get("id"), companyId),
        cb.isNull(root.get("company"))
    );
  }

}
