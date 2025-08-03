package com.invoicesync.datatransfer.dto;

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
public class DataTransferResponseDto {
  private Long id;

  private String fileName;

  private LocalDateTime createdAt;

  private String status;

  private List<DataTransferHeaderMappingDto> mappings;

  private String data;
}
