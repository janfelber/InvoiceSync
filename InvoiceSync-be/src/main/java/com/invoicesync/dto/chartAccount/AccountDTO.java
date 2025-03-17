package com.invoicesync.dto.chartAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
  private String accountId;
  private String accountName;
  private boolean editable;
}
