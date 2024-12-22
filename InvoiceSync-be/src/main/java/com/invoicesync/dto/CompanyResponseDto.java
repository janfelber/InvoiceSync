package com.invoicesync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyResponseDto {
  private Long id;
  private String name;
}
