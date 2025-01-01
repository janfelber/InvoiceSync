package com.invoicesync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class XmlFileResponseDto {
    private Long importId;
    private String fileName;
    private Date createdAt;
}
