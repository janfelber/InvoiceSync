package com.invoicesync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class XmlFileRequestDto {
    private Long userId;
    private String fileName;
    private String xmlContent;
    private Date createdAt;
}
