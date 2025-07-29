package com.invoicesync.convert;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.convert.dto.ConvertResponseDto;
import com.invoicesync.shared.common.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/convert")
@RequiredArgsConstructor
public class ConvertController {

  private final ConvertService convertService;

  @GetMapping("/imports")
  public ResponseEntity<PageResponse<ConvertResponseDto>> findAllConvertImportsByUser(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      final Authentication connectedUser) {
    return ResponseEntity.ok(convertService.findImportByUser(page, size, connectedUser));
  }

  @GetMapping("/{convert-id}")
  public ResponseEntity<ConvertResponseDto> getConvertById(@PathVariable("convert-id") final Long convertId) {
    return ResponseEntity.ok(convertService.findById(convertId));
  }

  @PostMapping("/save")
  public ResponseEntity<Long> save(@RequestParam("file") final MultipartFile file,
      final Authentication connectedUser) {
    return ResponseEntity.ok(convertService.saveConvert(file, connectedUser));
  }

}
