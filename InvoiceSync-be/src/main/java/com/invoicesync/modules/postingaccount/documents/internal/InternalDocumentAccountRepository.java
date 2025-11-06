package com.invoicesync.modules.postingaccount.documents.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.invoicesync.modules.company.model.Company;

@Repository
public interface InternalDocumentAccountRepository
    extends JpaRepository<InternalDocumentAccount, Long>, JpaSpecificationExecutor<InternalDocumentAccount> {

  boolean existsByCompanyAndAccountId(Company company, String accountId);

}
