package com.invoicesync.postingaccount.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

  private String categoryId;
  private String categoryName;

  private List<AccountDTO> accounts = new ArrayList<>();
}
