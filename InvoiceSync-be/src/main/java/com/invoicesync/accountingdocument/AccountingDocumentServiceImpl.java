package com.invoicesync.accountingdocument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.invoicesync.company.Company;
import com.invoicesync.company.NumberConfigType;
import com.invoicesync.shared.exception.NumberConfigMissingException;

@Service
public class AccountingDocumentServiceImpl implements AccountDocumentNumberService {

  @Override
  public void checkNumberConfiguration(final Company company, final NumberConfigType numberConfigType) {
    if (company == null) {
      throw new IllegalArgumentException("Company not found.");
    }

    final boolean isNumberConfigSet = !switch (numberConfigType) {
      case RECEIPT_CASH -> company.getCashReceiptNumber() != null;
      case RECEIPT_CARD -> company.getCardReceiptNumber() != null;
    };

    if (isNumberConfigSet) {
      throw new NumberConfigMissingException("Number configuration for " + numberConfigType + " is not set.");
    }
  }

  @Override
  public String incrementDocumentNumber(final String number) {
    final Matcher matcher = Pattern.compile("(\\d+)$").matcher(number);
    if (matcher.find()) {
      final String digits = matcher.group(1);
      final int length = digits.length();
      final int next = Integer.parseInt(digits) + 1;
      final String prefix = number.substring(0, matcher.start(1));
      return prefix + String.format("%0" + length + "d", next);
    } else {
      return number + "1";
    }
  }

}
