package com.invoicesync.postingaccount;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.postingaccount.dto.AddedAccountDTO;
import com.invoicesync.postingaccount.dto.ClassDTO;
import com.invoicesync.postingaccount.dto.PostingAccountRequest;
import com.invoicesync.postingaccount.dto.PostingAccountResponse;
import com.invoicesync.shared.common.PageResponse;

public interface PostingAccountService {

  List<ClassDTO> getAvailableAccount(Long companyId, PostingAccountType type, Authentication connectedUser);

  /**
   * Retrieves all posting accounts grouped by class for a given company.
   *
   * @param companyId ID of the company
   * @return list of posting accounts grouped by class
   */
  PageResponse<PostingAccountResponse> getAllPostingAccounts(int page, int size, PostingAccountType type,
      Long companyId, Authentication connectedUser);

  /**
   * Imports posting accounts from an external file (e.g., CSV, Excel) into the system.
   *
   * @param file uploaded file containing posting accounts
   * @param companyId ID of the company
   * @return list of successfully added accounts
   */
  List<AddedAccountDTO> importExternalPostingAccounts(MultipartFile file, final Long companyId);

  /**
   * Saves a new posting account or updates an existing one based on the request.
   *
   * @param request posting account data
   * @return ID of the saved posting account
   */
  Long savePostingAccount(final PostingAccountRequest request);

  void deletePostingAccount(final Long accountId, Authentication connectedUser);

}
