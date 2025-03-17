package com.invoicesync.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.dto.chartAccount.ClassDTO;
import com.invoicesync.service.ChartAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chart-account")
public class ChartAccountController {

  private final ChartAccountService chartAccountService;

  @GetMapping("/categories")
  public ResponseEntity<Map<String, ClassDTO>> getChartAccountsByCategory(@RequestParam final String classId) {
    return ResponseEntity.ok(chartAccountService.getChartAccountsByClass(classId));
  }

}
