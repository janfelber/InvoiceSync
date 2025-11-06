package com.invoicesync.modules.accountingdocument;

import com.invoicesync.core.enums.NumberConfigType;
import com.invoicesync.modules.company.model.Company;

public interface AccountDocumentNumberService {

  void checkNumberConfiguration(Company company, NumberConfigType numberConfigType);

  String incrementDocumentNumber(String number);

}
