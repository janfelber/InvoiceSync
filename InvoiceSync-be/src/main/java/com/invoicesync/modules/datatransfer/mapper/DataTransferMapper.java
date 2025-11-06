package com.invoicesync.modules.datatransfer.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.invoicesync.modules.datatransfer.dto.DataTransferHeaderMappingDto;
import com.invoicesync.modules.datatransfer.dto.DataTransferResponseDto;
import com.invoicesync.modules.datatransfer.model.DataTransfer;

@Service
public class DataTransferMapper {

  public DataTransferResponseDto toConvertResponse(final DataTransfer dataTransfer) {
    final List<DataTransferHeaderMappingDto> mappings = dataTransfer.getMappings().stream()
        .map(mapping -> DataTransferHeaderMappingDto.builder()
            .id(mapping.getId())
            .convertId(dataTransfer.getId())
            .originalHeader(mapping.getOriginalHeader())
            .mappedHeader(mapping.getMappedHeader())
            .isIncluded(mapping.isIncluded())
            .build())
        .collect(Collectors.toList());

    return DataTransferResponseDto.builder()
        .id(dataTransfer.getId())
        .fileName(dataTransfer.getFileName())
        .createdAt(dataTransfer.getCreatedDate())
        .status(dataTransfer.getStatus())
        .mappings(mappings)
        .data(dataTransfer.getData())
        .build();
  }

  public DataTransferResponseDto toConvertTableResponse(final DataTransfer dataTransfer) {
    return DataTransferResponseDto.builder()
        .id(dataTransfer.getId())
        .fileName(dataTransfer.getFileName())
        .createdAt(dataTransfer.getCreatedDate())
        .status(dataTransfer.getStatus())
        .build();
  }

}
