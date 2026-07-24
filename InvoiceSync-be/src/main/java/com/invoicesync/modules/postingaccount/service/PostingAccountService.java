package com.invoicesync.modules.postingaccount.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.enums.PostingAccountType;
import com.invoicesync.modules.postingaccount.model.AddedAccountDTO;
import com.invoicesync.modules.postingaccount.model.ClassDTO;
import com.invoicesync.modules.postingaccount.model.PostingAccountRequest;
import com.invoicesync.modules.postingaccount.model.PostingAccountResponse;

public interface PostingAccountService {

  List<ClassDTO> getAvailableAccount(Long companyId, PostingAccountType type, Authentication connectedUser);

  /**
   * Retrieves all posting accounts grouped by class for a given company.
   *
   * @param companyId ID of the company
   * @return list of posting accounts grouped by class
   */
  PageResponse<PostingAccountResponse> getAllPostingAccounts(int page, int size, PostingAccountType type,
      Long companyId);

  /**
   * Imports posting accounts from an external file (e.g., CSV, Excel) into the system.
   *
   * @param file uploaded file containing posting accounts
   * @param companyId ID of the company
   * @return list of successfully added accounts
   */
  List<AddedAccountDTO> importExternalPostingAccounts(MultipartFile file, Long companyId);

  /**
   * Saves a new posting account or updates an existing one based on the request.
   *
   * @param request posting account data
   * @return ID of the saved posting account
   */
  Long savePostingAccount(PostingAccountRequest request);

  void deletePostingAccount(Long accountId, Authentication connectedUser);

}
