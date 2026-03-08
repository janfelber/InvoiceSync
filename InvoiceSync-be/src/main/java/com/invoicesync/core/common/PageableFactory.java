package com.invoicesync.core.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableFactory {

  public static final int DEFAULT_PAGE = 0;

  public static final int DEFAULT_SIZE = 10;

  private PageableFactory() {
  }

  public static Pageable ofAscending(int page, int size) {
    return PageRequest.of(page, size, Sort.by("createdDate").ascending());
  }

  public static Pageable ofDescending(int page, int size) {
    return PageRequest.of(page, size, Sort.by("createdDate").descending());
  }

  public static Pageable of(int page, int size) {
    return PageRequest.of(page, size);
  }

}
