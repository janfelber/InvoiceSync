package com.invoicesync.modules.xmlFile.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.xmlFile.dto.XmlFileResponseDto;
import com.invoicesync.modules.xmlFile.service.XmlFileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/xml-file")
public class XmlFileController {

  private final XmlFileService xmlFileService;

  @GetMapping("/user")
  public ResponseEntity<PageResponse<XmlFileResponseDto>> getCurrentUserXmlFiles(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      final Authentication connectedUser) {
    return ResponseEntity.ok(xmlFileService.finalAllXmlImportsByUser(page, size, connectedUser));
  }

  @PostMapping("/save")
  public ResponseEntity<List<Long>> saveXmlFiles(@RequestParam("file") final MultipartFile[] files,
      final Authentication connectedUser) {
    final List<Long> savedIds = Arrays.stream(files)
        .map(file -> xmlFileService.saveXmlFile(file, connectedUser))
        .toList();

    return ResponseEntity.status(HttpStatus.CREATED).body(savedIds);
  }

  //
  @PostMapping("/generate-zip/{importId}")
  public ResponseEntity<byte[]> processAndGenerateZip(@PathVariable final Long importId,
      final Authentication connectedUser) {
    try {

      final byte[] zipContent = xmlFileService.processAndGenerateZip(importId, connectedUser);
      final HttpHeaders headers = new HttpHeaders();

      headers.setContentDisposition(
          ContentDisposition.builder("attachment").filename("generated_xml.zip").build());
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

      // Return the zip content as a byte array for download
      return ResponseEntity.ok().headers(headers).body(zipContent);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

}