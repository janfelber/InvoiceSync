package com.invoicesync.modules.ocr;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface OCRService {

  String extractTextFromPDF(final MultipartFile file, final Authentication connectedUser);
}
