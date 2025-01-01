package com.invoicesync.service;

import com.invoicesync.dto.XmlFileRequestDto;
import com.invoicesync.dto.XmlFileResponseDto;
import com.invoicesync.module.XmlFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface XmlFileService {

    //save xml file
    XmlFile saveXmlFile(MultipartFile file) throws IOException;

    //get xml file by user id
    List<XmlFileResponseDto> getXmlFilesByUserId(Long userId);

    //get xml file by import id
    String getXmlContentByImportId(Long importId);
}
