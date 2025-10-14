package com.invoicesync.postingaccount;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostingAccountType {

  INTERNAL,
  CASH
}
