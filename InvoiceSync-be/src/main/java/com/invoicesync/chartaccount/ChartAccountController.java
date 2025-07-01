package com.invoicesync.chartaccount;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.chartaccount.dto.AddedAccountDTO;
import com.invoicesync.chartaccount.dto.ChartAccountRequest;
import com.invoicesync.chartaccount.dto.ClassDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chart-account")
public class ChartAccountController {

  private final ChartAccountService chartAccountService;

  @GetMapping("/{companyId}/accounts")
  public ResponseEntity<List<ClassDTO>> getAllChartAccountsByClass(@PathVariable final Long companyId) {
    final List<ClassDTO> classes = chartAccountService.getAllChartAccountsByClass(companyId);
    return ResponseEntity.ok(classes);
  }

  @PostMapping("/import")
  public ResponseEntity<List<AddedAccountDTO>> readSimple(@RequestParam("file") final MultipartFile file,
      @RequestParam("companyId") final Long companyId) {
    final List<AddedAccountDTO> added = chartAccountService.importExternalChartAccounts(file, companyId);
    return ResponseEntity.ok(added);
  }

  @PostMapping("/save")
  public ResponseEntity<Long> saveAccountChart(
      @Valid @RequestBody final ChartAccountRequest request
  ) {
    System.out.println("Controller Request: " + request);
    return ResponseEntity.ok(chartAccountService.saveChartAccount(request));
  }

}
