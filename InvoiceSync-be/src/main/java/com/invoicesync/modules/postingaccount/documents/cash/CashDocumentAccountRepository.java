package com.invoicesync.modules.postingaccount.documents.cash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CashDocumentAccountRepository extends JpaRepository<CashDocumentAccount, Long>,
                                                       JpaSpecificationExecutor<CashDocumentAccount> {

}
