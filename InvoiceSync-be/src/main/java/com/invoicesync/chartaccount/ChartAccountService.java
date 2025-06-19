package com.invoicesync.chartaccount;

import java.util.List;

import com.invoicesync.chartaccount.dto.ClassDTO;

public interface ChartAccountService {

  List<ClassDTO> getAllChartAccountsByClass();

}
