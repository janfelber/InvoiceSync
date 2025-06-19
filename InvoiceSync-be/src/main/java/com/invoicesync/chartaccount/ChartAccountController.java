package com.invoicesync.chartaccount;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.chartaccount.dto.ClassDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chart-account")
public class ChartAccountController {

  private final ChartAccountService chartAccountService;

  @GetMapping("/all")
  public ResponseEntity<List<ClassDTO>> getAllChartAccountsByClass() {
    final List<ClassDTO> classes = chartAccountService.getAllChartAccountsByClass();
    return ResponseEntity.ok(classes);
  }

}
