package com.invoicesync.accountingdocument;

import com.invoicesync.company.Company;
import com.invoicesync.company.NumberConfigType;

public interface AccountDocumentNumberService {

  void checkNumberConfiguration(Company company, NumberConfigType numberConfigType);

  String incrementDocumentNumber(String number);

}
