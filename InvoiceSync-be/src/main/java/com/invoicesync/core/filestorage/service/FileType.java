package com.invoicesync.core.filestorage.service;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {

  PDF(List.of("pdf"), List.of("application/pdf")),
  JPEG(List.of("jpg", "jpeg"), List.of("image/jpeg")),
  PNG(List.of("png"), List.of("image/png")),
  XML(List.of("xml"), List.of("application/xml", "text/xml")),
  DOC(List.of("doc"), List.of("application/msword")),
  DOCX(List.of("docx"), List.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));

  private final List<String> extensions;

  private final List<String> mimeTypes;

}
