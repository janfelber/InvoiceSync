package com.invoicesync.ocr;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface OCRService {

  public String extractTextFromPDF(final MultipartFile file, final Authentication connectedUser) throws Exception;
}
