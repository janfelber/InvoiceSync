package com.invoicesync.postingaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostingAccountResponse {

  private Long id;

  private String classId;

  private String className;

  private String accountId;

  private String accountName;

  private String category;

  private String categoryName;

  private boolean isEditable;

}
