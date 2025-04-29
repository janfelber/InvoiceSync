package com.invoicesync.controller;

import java.io.InputStream;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.service.ConvertService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/convert")
@RequiredArgsConstructor
public class ConvertController {

  private final ConvertService convertService;

  @PostMapping("/to-pohoda")
  public ResponseEntity<byte[]> convertExcelToPohoda(@RequestParam("file") final MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {
      byte[] modifiedExcel = convertService.convertToPohoda(inputStream);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDisposition(ContentDisposition.attachment().filename("upraveny.xlsx").build());

      return new ResponseEntity<>(modifiedExcel, headers, HttpStatus.OK);

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(null);
    }
  }

}
