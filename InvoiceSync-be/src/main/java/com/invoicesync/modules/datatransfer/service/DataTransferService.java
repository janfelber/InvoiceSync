package com.invoicesync.modules.datatransfer.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.datatransfer.dto.DataTransferHeaderMappingDto;
import com.invoicesync.modules.datatransfer.dto.DataTransferResponseDto;

public interface DataTransferService {

  /**
   * Retrieves a paginated list of data imports associated with the connected user.
   *
   * @param size number of data imports per page
   * @param page page number (0-based)
   * @param connectedUser currently authenticated user
   * @return paginated response of data imports
   */
  PageResponse<DataTransferResponseDto> findImportByUser(int page, int size, Authentication connectedUser);

  /**
   * Creates a new data import record for the connected user.
   *
   * @param excelInputStream uploaded Excel file
   * @param connectedUser currently authenticated user
   * @return ID of the newly created or updated entity after conversion
   */
  Long saveConvert(MultipartFile excelInputStream, Authentication connectedUser);

  /**
   * Finds a data import by its unique ID.
   *
   * @param convertId ID of the data import
   * @return data import as a response DTO
   */
  DataTransferResponseDto findById(Long convertId);

  /**
   * Saves or updates the mapping of imported Excel data.
   *
   * @param convertId ID of the data import to update
   * @param mappings list of column-to-field mappings
   * @param connectedUser currently authenticated user
   */
  void saveMapping(Long convertId, List<DataTransferHeaderMappingDto> mappings, Authentication connectedUser);

  /**
   * Downloads the converted data from a previous Excel import.
   *
   * @param convertId ID of the data import to download
   * @param connectedUser currently authenticated user
   * @return byte array representing the exported file content
   */
  byte[] downloadConvert(Long convertId, Authentication connectedUser);

}
