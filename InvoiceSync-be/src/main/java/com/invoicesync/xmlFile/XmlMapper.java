package com.invoicesync.xmlFile;

import org.springframework.stereotype.Service;

import com.invoicesync.xmlFile.dto.XmlFileRequest;
import com.invoicesync.xmlFile.dto.XmlFileResponseDto;

@Service
public class XmlMapper {

  public XmlFile toXmlFile(final XmlFileRequest request) {
    return XmlFile.builder()
        .file_name(request.fileName())
        .xml_content(request.xmlContent())
        .build();
  }

  public XmlFileResponseDto toXmlTableResponse(final XmlFile xmlFile) {
    return XmlFileResponseDto.builder()
        .id(xmlFile.getId())
        .fileName(xmlFile.getFile_name())
        .createdAt(xmlFile.getCreatedDate())
        .build();
  }

}
