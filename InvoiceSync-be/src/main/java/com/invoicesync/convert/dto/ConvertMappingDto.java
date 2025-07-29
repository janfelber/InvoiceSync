package com.invoicesync.convert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConvertMappingDto {

  private Long id;

  private Long convertId;

  private String originalHeader;

  private String mappedHeader;

  private boolean isIncluded;

}
