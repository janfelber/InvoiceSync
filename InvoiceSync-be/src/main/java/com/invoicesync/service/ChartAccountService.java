package com.invoicesync.service;

import java.util.Map;

import com.invoicesync.dto.chartAccount.ClassDTO;

public interface ChartAccountService {

  Map<String, ClassDTO> getChartAccountsByClass(String classId);

}
