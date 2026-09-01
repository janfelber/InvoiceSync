package com.invoicesync.core.filestorage.service;

import java.io.IOException;
import java.util.Arrays;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.exception.InvalidFileTypeException;

@Service
public class FileValidator {

  private final Tika tika;

  public FileValidator(final Tika tika) {
    this.tika = tika;
  }

  public void validate(final MultipartFile sourceDocument) {
    final String fileExtension = getFileExtension(sourceDocument.getOriginalFilename());

    if (fileExtension == null) {
      throw new InvalidFileTypeException("Invalid file extension");
    }

    final FileType foundType = Arrays.stream(FileType.values())
        .filter(type -> type.getExtensions().contains(fileExtension))
        .findFirst()
        .orElseThrow(() -> new InvalidFileTypeException("Unsupported file extension: " + fileExtension));
    try {
      final String tikaMimeType = tika.detect(sourceDocument.getInputStream(), sourceDocument.getOriginalFilename());

      if (!foundType.getMimeTypes().contains(sourceDocument.getContentType())) {
        throw new InvalidFileTypeException("Declared content type does not match file extension");
      }

      if (!foundType.getMimeTypes().contains(tikaMimeType)) {
        throw new InvalidFileTypeException("Actual file content does not match declared file type");
      }

    } catch (IOException e) {
      throw new InvalidFileTypeException("Failed to read file content for validation");
    }
  }

  private String getFileExtension(final String fileName) {
    if (fileName == null || fileName.isEmpty()) {
      return null;
    }
    final int lastDotIndex = fileName.lastIndexOf('.');

    if (lastDotIndex == -1) {
      return "";
    }

    return fileName.substring(lastDotIndex + 1).toLowerCase();

  }

}
