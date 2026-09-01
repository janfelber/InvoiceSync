package com.invoicesync.core.filestorage.service;

import static java.lang.System.currentTimeMillis;

import java.io.IOException;
import java.util.List;

import jakarta.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

  private static final String FILES_SEPARATOR = "/";

  private final S3Client s3Client;

  private final FileValidator fileValidator;

  @Value("${r2.bucket}")
  private String bucket;

  @Autowired
  public FileStorageServiceImpl(final S3Client s3Client,
      final FileValidator fileValidator) {
    this.s3Client = s3Client;
    this.fileValidator = fileValidator;
  }

  @Override
  public String saveInvoiceDocument(@Nonnull final MultipartFile sourceDocument,
      @Nonnull final Long invoiceId, @Nonnull final String connectedUserId) {
    final String fileUploadSubPath = connectedUserId + FILES_SEPARATOR + "invoices" + FILES_SEPARATOR + invoiceId;
    return uploadFile(sourceDocument, fileUploadSubPath);
  }

  @Override
  public String saveReceiptDocument(final MultipartFile document, final Long receiptId, final String connectedUserId) {
    final String fileUploadSubPath =
        connectedUserId + FILES_SEPARATOR + "receipts" + FILES_SEPARATOR + receiptId;
    return uploadFile(document, fileUploadSubPath);
  }

  @Override
  public void deleteDocument(final String key) {
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.deleteObject(deleteObjectRequest);
  }

  @Override
  public void deleteBatchDocuments(final List<String> keys) {
    DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
        .bucket(bucket)
        .delete(Delete.builder()
            .objects(keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList())
            .build())
        .build();

    s3Client.deleteObjects(deleteObjectsRequest);
  }

  private String uploadFile(@Nonnull final MultipartFile sourceDocument, @Nonnull final String fileUploadSubPath) {
    fileValidator.validate(sourceDocument);

    final String fileExtension = getFileExtension(sourceDocument.getOriginalFilename());
    final String key = fileUploadSubPath + FILES_SEPARATOR + currentTimeMillis() + "." + fileExtension;
    try {
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      s3Client.putObject(request, RequestBody.fromBytes(sourceDocument.getBytes()));
      return key;
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

}
