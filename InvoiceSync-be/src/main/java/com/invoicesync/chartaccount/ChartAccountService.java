package com.invoicesync.chartaccount;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.chartaccount.dto.AddedAccountDTO;
import com.invoicesync.chartaccount.dto.ChartAccountRequest;
import com.invoicesync.chartaccount.dto.ClassDTO;

public interface ChartAccountService {


  /**
   * Retrieves all chart accounts grouped by class for a given company.
   *
   * @param companyId ID of the company
   * @return list of chart accounts grouped by class
   */
  List<ClassDTO> getAllChartAccountsByClass(Long companyId);

  /**
   * Imports chart accounts from an external file (e.g., CSV, Excel) into the system.
   *
   * @param file uploaded file containing chart accounts
   * @param companyId ID of the company
   * @return list of successfully added accounts
   */
  List<AddedAccountDTO> importExternalChartAccounts(MultipartFile file, final Long companyId);

  /**
   * Saves a new chart account or updates an existing one based on the request.
   *
   * @param request chart account data
   * @return ID of the saved chart account
   */
  Long saveChartAccount(final ChartAccountRequest request);

}
