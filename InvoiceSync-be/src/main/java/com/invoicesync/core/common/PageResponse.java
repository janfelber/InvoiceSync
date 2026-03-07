package com.invoicesync.core.common;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

  private List<T> content;

  private int number;

  private int size;

  private Long totalElements;

  private int totalPages;

  private boolean first;

  private boolean last;

  public static <T, R> PageResponse<R> from(Page<T> page, Function<T, R> mapper) {
    return new PageResponse<>(
        page.stream().map(mapper).toList(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast()
    );
  }

}
