package com.invoicesync.convert.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConvertResponseDto {
  private Long id;

  private String fileName;

  private LocalDateTime createdAt;

  private String status;

  private List<ConvertMappingDto> mappings;

  private String data;
}
