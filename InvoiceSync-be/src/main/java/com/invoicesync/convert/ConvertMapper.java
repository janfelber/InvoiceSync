package com.invoicesync.convert;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.invoicesync.convert.dto.ConvertMappingDto;
import com.invoicesync.convert.dto.ConvertResponseDto;

@Service
public class ConvertMapper {

  public ConvertResponseDto toConvertResponse(final Convert convert) {
    final List<ConvertMappingDto> mappings = convert.getMappings().stream()
        .map(mapping -> ConvertMappingDto.builder()
            .id(mapping.getId())
            .convertId(convert.getId())
            .originalHeader(mapping.getOriginalHeader())
            .mappedHeader(mapping.getMappedHeader())
            .isIncluded(mapping.isIncluded())
            .build())
        .collect(Collectors.toList());

    return ConvertResponseDto.builder()
        .id(convert.getId())
        .fileName(convert.getFileName())
        .createdAt(convert.getCreatedDate())
        .status(convert.getStatus())
        .mappings(mappings)
        .data(convert.getData())
        .build();
  }

  public ConvertResponseDto toConvertTableResponse(final Convert convert) {
    return ConvertResponseDto.builder()
        .id(convert.getId())
        .fileName(convert.getFileName())
        .createdAt(convert.getCreatedDate())
        .status(convert.getStatus())
        .build();
  }

}
