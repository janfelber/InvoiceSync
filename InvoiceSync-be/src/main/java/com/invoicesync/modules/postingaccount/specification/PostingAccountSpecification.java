package com.invoicesync.modules.postingaccount.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.modules.postingaccount.documents.cash.CashDocumentAccount;
import com.invoicesync.modules.postingaccount.documents.internal.InternalDocumentAccount;

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
