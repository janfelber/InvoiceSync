package com.invoicesync.core.filestorage.service;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.receipt.model.Receipt;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

  @Value("${file.upload.documents-output-path}")
  private String fileUploadPath;

  @Override
  public String saveFile(@Nonnull final MultipartFile sourceDocument,
      @Nonnull final Invoice invoice, @Nonnull final String connectedUserId) {
    final String fileUploadSubPath = "users" + separator + connectedUserId + separator + invoice.getId();
    return uploadFile(sourceDocument, fileUploadSubPath);
  }

  @Override
  public String saveReceiptFile(final MultipartFile document, final Receipt receiptId, final String connectedUserId) {
    final String fileUploadSubPath =
        "users" + separator + connectedUserId + separator + "receipts" + separator + receiptId.getId();
    return uploadFile(document, fileUploadSubPath);
  }

  private String uploadFile(@Nonnull final MultipartFile sourceDocument, @Nonnull final String fileUploadSubPath) {
    final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
    final File targetFolder = new File(finalUploadPath);

    if (!targetFolder.exists()) {
      final boolean isFolderCreated = targetFolder.mkdirs();
      if (!isFolderCreated) {
        log.warn("Failed to create folder: {}", targetFolder.getAbsolutePath());
        return null;
      }
    }
    final String fileExtension = getFileExtension(sourceDocument.getOriginalFilename());
    final String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + fileExtension;
    final Path targetPath = Paths.get(targetFilePath);
    try {
      Files.write(targetPath, sourceDocument.getBytes());
      log.info("File was saved to: {}", targetPath.toAbsolutePath());
      return targetFilePath;
    } catch (IOException e) {
      log.error("File was not save", e);
    }

    return null;

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

  // @Override
  // public String saveFile(@Nonnull final MultipartFile document,
  //     @Nonnull final Invoice invoice, @Nonnull final Authentication connectedUser) {
  //   final String fileUploadSubPath = "users" + File.separator + us
  //   return "";
  // }

}
