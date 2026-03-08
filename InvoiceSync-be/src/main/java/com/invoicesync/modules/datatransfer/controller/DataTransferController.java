package com.invoicesync.modules.datatransfer.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.Api;
import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.common.PageableFactory;
import com.invoicesync.modules.datatransfer.dto.DataTransferHeaderMappingDto;
import com.invoicesync.modules.datatransfer.dto.DataTransferResponseDto;
import com.invoicesync.modules.datatransfer.service.DataTransferService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Api.CONVERT)
@RequiredArgsConstructor
public class DataTransferController {

  private final DataTransferService dataTransferService;

  @GetMapping(Api.CONVERT_GET_IMPORTS)
  public ResponseEntity<PageResponse<DataTransferResponseDto>> findAllConvertImportsByUser(
      @RequestParam(name = "page", defaultValue = "" + PageableFactory.DEFAULT_PAGE, required = false) final int page,
      @RequestParam(name = "size", defaultValue = "" + PageableFactory.DEFAULT_SIZE, required = false) final int size,
      final Authentication connectedUser) {
    return ResponseEntity.ok(dataTransferService.findImportByUser(page, size, connectedUser));
  }

  @GetMapping(Api.CONVERT_GET_BY_ID)
  public ResponseEntity<DataTransferResponseDto> getConvertById(@PathVariable("convert-id") final Long convertId) {
    return ResponseEntity.ok(dataTransferService.findById(convertId));
  }

  @PostMapping(Api.SAVE)
  public ResponseEntity<Long> save(@RequestParam("file") final MultipartFile file,
      final Authentication connectedUser) {
    return ResponseEntity.ok(dataTransferService.saveConvert(file, connectedUser));
  }

  @PostMapping(Api.CONVERT_SAVE_MAPPING)
  public ResponseEntity<Void> saveMapping(
      @PathVariable final Long convertId,
      @RequestBody final List<DataTransferHeaderMappingDto> mappings,
      final Authentication connectedUser
  ) {
    dataTransferService.saveMapping(convertId, mappings, connectedUser);
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = Api.CONVERT_DOWNLOAD, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<byte[]> downloadConvert(@PathVariable final Long convertId,
      final Authentication connectedUser) {
    final byte[] newExcel = dataTransferService.downloadConvert(convertId, connectedUser);

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(
        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    headers.setContentDisposition(
        ContentDisposition.attachment().filename("mapped.xlsx").build());
    return new ResponseEntity<>(newExcel, headers, HttpStatus.OK);
  }

}
