package com.invoicesync.modules.postingaccount.contoller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.Api;
import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.common.PageableFactory;
import com.invoicesync.core.enums.PostingAccountType;
import com.invoicesync.modules.postingaccount.model.AddedAccountDTO;
import com.invoicesync.modules.postingaccount.model.ClassDTO;
import com.invoicesync.modules.postingaccount.model.PostingAccountRequest;
import com.invoicesync.modules.postingaccount.model.PostingAccountResponse;
import com.invoicesync.modules.postingaccount.service.PostingAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Api.POST_ACCOUNT)
public class PostingAccountController {

  private final PostingAccountService postingAccountService;

  @GetMapping(Api.POST_ACCOUNT_GET_BY_COMPANY)
  public ResponseEntity<PageResponse<PostingAccountResponse>> getAllPostingAccounts(
      @RequestParam(name = "page", defaultValue = "" + PageableFactory.DEFAULT_PAGE, required = false) final int page,
      @RequestParam(name = "size", defaultValue = "" + PageableFactory.DEFAULT_SIZE, required = false) final int size,
      @RequestParam(name = "type", defaultValue = "INTERNAL") final PostingAccountType type,
      @PathVariable("companyId") final Long companyId, final Authentication connectedUser) {
    return ResponseEntity.ok(postingAccountService.getAllPostingAccounts(page, size, type, companyId, connectedUser));
  }

  @GetMapping(Api.POST_ACCOUNT_GET_CLASSES)
  public ResponseEntity<List<ClassDTO>> getAvailableAccount(@PathVariable("companyId") final Long companyId,
      @RequestParam(name = "type", defaultValue = "INTERNAL") final PostingAccountType type,
      final Authentication connectedUser) {
    return ResponseEntity.ok(postingAccountService.getAvailableAccount(companyId, type, connectedUser));
  }

  @PostMapping(Api.POST_ACCOUNT_IMPORT)
  public ResponseEntity<List<AddedAccountDTO>> readSimple(@RequestParam("file") final MultipartFile file,
      @RequestParam("companyId") final Long companyId) {
    final List<AddedAccountDTO> added = postingAccountService.importExternalPostingAccounts(file, companyId);
    return ResponseEntity.ok(added);
  }

  @PostMapping(Api.SAVE)
  public ResponseEntity<Long> savePostingAccount(
      @Valid @RequestBody final PostingAccountRequest request
  ) {
    return ResponseEntity.ok(postingAccountService.savePostingAccount(request));
  }

  @DeleteMapping(Api.POST_ACCOUNT_DELETE)
  public void deletePostingAccount(@PathVariable final Long accountId, final Authentication connectedUser) {
    postingAccountService.deletePostingAccount(accountId, connectedUser);
  }

}
