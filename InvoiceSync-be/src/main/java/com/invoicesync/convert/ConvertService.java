package com.invoicesync.convert;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.convert.dto.ConvertResponseDto;
import com.invoicesync.shared.common.PageResponse;

public interface ConvertService {

  PageResponse<ConvertResponseDto> findImportByUser(int page, int size, Authentication connectedUser);

  Long saveConvert(MultipartFile excelInputStream, Authentication connectedUser);

  ConvertResponseDto findById(Long convertId);

}
