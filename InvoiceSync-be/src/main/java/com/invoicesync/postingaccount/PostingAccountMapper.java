package com.invoicesync.postingaccount;

import org.springframework.stereotype.Service;

import com.invoicesync.company.Company;
import com.invoicesync.company.CompanyRepository;
import com.invoicesync.postingaccount.documents.cash.CashDocumentAccount;
import com.invoicesync.postingaccount.documents.internal.InternalDocumentAccount;
import com.invoicesync.postingaccount.dto.PostingAccountRequest;
import com.invoicesync.postingaccount.dto.PostingAccountResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostingAccountMapper {

  private final CompanyRepository companyRepository;

  public PostingAccountResponse toPostingAccountTableResponse(final BaseDocumentAccountEntity internalDocumentAccount) {
    return PostingAccountResponse.builder()
        .id(internalDocumentAccount.getId())
        .classId(internalDocumentAccount.getClassId())
        .className(internalDocumentAccount.getClassName())
        .accountId(internalDocumentAccount.getAccountId())
        .accountName(internalDocumentAccount.getAccountName())
        .category(internalDocumentAccount.getCategory())
        .categoryName(internalDocumentAccount.getCategoryName())
        .isEditable(internalDocumentAccount.isEditable())
        .build();
  }

  public BaseDocumentAccountEntity toPostingAccount(final PostingAccountRequest request) {
    final Company company = companyRepository.findById(request.companyId()).orElseThrow();

    final BaseDocumentAccountEntity account = switch (request.type()) {
      case CASH -> new CashDocumentAccount();
      case INTERNAL -> new InternalDocumentAccount();
    };

    account.setCompany(company);
    account.setClassId("99");
    account.setClassName("Vlastné účty");
    account.setAccountId(request.accountId());
    account.setAccountName(request.accountName());
    account.setCategory(AccountUtils.getCategoryIdFromAccount(request.accountId()));
    account.setCategoryName(AccountUtils.getCategoryIdFromAccount(request.accountId()));
    account.setEditable(true);

    return account;
  }

}
