package com.invoicesync.postingaccount;

import java.util.List;

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

import com.invoicesync.postingaccount.dto.AddedAccountDTO;
import com.invoicesync.postingaccount.dto.ClassDTO;
import com.invoicesync.postingaccount.dto.PostingAccountRequest;
import com.invoicesync.postingaccount.dto.PostingAccountResponse;
import com.invoicesync.shared.common.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post-account")
public class PostingAccountController {

  private final PostingAccountService postingAccountService;

  @GetMapping("/{companyId}/accounts")
  public ResponseEntity<PageResponse<PostingAccountResponse>> getAllPostingAccounts(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      @RequestParam(name = "type", defaultValue = "INTERNAL") final PostingAccountType type,
      @PathVariable("companyId") final Long companyId, final Authentication connectedUser) {
    return ResponseEntity.ok(postingAccountService.getAllPostingAccounts(page, size, type, companyId, connectedUser));
  }

  @GetMapping("/{companyId}/classes")
  public ResponseEntity<List<ClassDTO>> getAvailableAccount(@PathVariable("companyId") final Long companyId,
      @RequestParam(name = "type", defaultValue = "INTERNAL") final PostingAccountType type,
      final Authentication connectedUser) {
    return ResponseEntity.ok(postingAccountService.getAvailableAccount(companyId, type, connectedUser));
  }

  @PostMapping("/import")
  public ResponseEntity<List<AddedAccountDTO>> readSimple(@RequestParam("file") final MultipartFile file,
      @RequestParam("companyId") final Long companyId) {
    final List<AddedAccountDTO> added = postingAccountService.importExternalPostingAccounts(file, companyId);
    return ResponseEntity.ok(added);
  }

  @PostMapping("/save")
  public ResponseEntity<Long> savePostingAccount(
      @Valid @RequestBody final PostingAccountRequest request
  ) {
    return ResponseEntity.ok(postingAccountService.savePostingAccount(request));
  }

  @DeleteMapping("/{accountId}")
  public void deletePostingAccount(@PathVariable final Long accountId, final Authentication connectedUser) {
    postingAccountService.deletePostingAccount(accountId, connectedUser);
  }

}
