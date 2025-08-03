package com.invoicesync.datatransfer;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.datatransfer.dto.DataTransferHeaderMappingDto;
import com.invoicesync.datatransfer.dto.DataTransferResponseDto;
import com.invoicesync.shared.common.PageResponse;

public interface DataTransferService {

  PageResponse<DataTransferResponseDto> findImportByUser(int page, int size, Authentication connectedUser);

  Long saveConvert(MultipartFile excelInputStream, Authentication connectedUser);

  DataTransferResponseDto findById(Long convertId);

  void saveMapping(Long convertId, List<DataTransferHeaderMappingDto> mappings, Authentication connectedUser);

  byte[] downloadConvert(Long convertId, Authentication connectedUser);

}
