package com.invoicesync.modules.xmlFile.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.xmlFile.dto.XmlFileResponseDto;

public interface XmlFileService {

  /**
   * Saves an uploaded XML file for the connected user.
   *
   * @param file uploaded XML file
   * @param connectedUser currently authenticated user
   * @return ID of the newly saved XML import
   */
  Long saveXmlFile(MultipartFile file, Authentication connectedUser);

  /**
   * Retrieves a paginated list of XML imports for the connected user.
   *
   * @param size number of items per page
   * @param page page number (0-based)
   * @param connectedUser currently authenticated user
   * @return paginated response of XML file imports
   */
  PageResponse<XmlFileResponseDto> finalAllXmlImportsByUser(int size, int page, Authentication connectedUser);

  /**
   * Processes the XML import identified by importId and generates a ZIP file.
   *
   * @param importId ID of the XML import to process
   * @param connectedUser currently authenticated user
   * @return byte array representing the generated ZIP file
   * @throws Exception if processing fails
   */
  byte[] processAndGenerateZip(Long importId, Authentication connectedUser) throws Exception;

}
