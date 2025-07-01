package com.invoicesync.chartaccount;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.chartaccount.dto.AddedAccountDTO;
import com.invoicesync.chartaccount.dto.ChartAccountRequest;
import com.invoicesync.chartaccount.dto.ClassDTO;

public interface ChartAccountService {

  List<ClassDTO> getAllChartAccountsByClass(Long companyId);

  List<AddedAccountDTO> importExternalChartAccounts(MultipartFile file, final Long companyId);

  Long saveChartAccount(final ChartAccountRequest request);

}
