package com.invoicesync.xmlFile;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.shared.common.PageResponse;
import com.invoicesync.xmlFile.dto.XmlFileResponseDto;

public interface XmlFileService {

    //save xml file
    Long saveXmlFile(MultipartFile file, Authentication connectedUser);

    //get xml file by user id
    PageResponse<XmlFileResponseDto> finalAllXmlImportsByUser(int size, int page, Authentication connectedUser);

    //get xml file by import id
    String getXmlContentByImportId(Long importId);
}
