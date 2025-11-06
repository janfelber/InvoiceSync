package com.invoicesync.modules.xmlFile.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XmlFileResponseDto {

    private Long id;

    private String fileName;

    private LocalDateTime createdAt;

}
