package com.invoicesync.modules.xmlFile.mapper;

import org.springframework.stereotype.Service;

import com.invoicesync.modules.xmlFile.dto.XmlFileRequest;
import com.invoicesync.modules.xmlFile.dto.XmlFileResponseDto;
import com.invoicesync.modules.xmlFile.model.XmlFile;

@Service
public class XmlMapper {

  public XmlFile toXmlFile(final XmlFileRequest request) {
    return XmlFile.builder()
        .fileName(request.fileName())
        .xmlContent(request.xmlContent())
        .build();
  }

  public XmlFileResponseDto toXmlTableResponse(final XmlFile xmlFile) {
    return XmlFileResponseDto.builder()
        .id(xmlFile.getId())
        .fileName(xmlFile.getFileName())
        .createdAt(xmlFile.getCreatedDate())
        .build();
  }

}
