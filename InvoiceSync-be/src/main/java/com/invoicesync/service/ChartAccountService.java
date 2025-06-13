package com.invoicesync.service;

import java.util.List;

import com.invoicesync.dto.chartAccount.ClassDTO;

public interface ChartAccountService {

  List<ClassDTO> getAllChartAccountsByClass();

}
