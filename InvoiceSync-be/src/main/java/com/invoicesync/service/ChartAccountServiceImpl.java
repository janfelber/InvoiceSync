package com.invoicesync.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.invoicesync.dto.chartAccount.AccountDTO;
import com.invoicesync.dto.chartAccount.CategoryDTO;
import com.invoicesync.dto.chartAccount.ClassDTO;
import com.invoicesync.module.ChartAccount;
import com.invoicesync.repository.ChartAccountRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChartAccountServiceImpl implements ChartAccountService {

  private final ChartAccountRepository chartAccountRepository;

  @Override
  public Map<String, ClassDTO> getChartAccountsByClass(final String classId) {
    final List<ChartAccount> chartAccounts = chartAccountRepository.findAll();

    // Zoskup a transformuj dáta do požadovaného formátu
    Map<String, ClassDTO> result = chartAccounts.stream()
        .filter(chartAccount -> classId.equals(chartAccount.getClassId())) // Filtrovanie kategórie
        .collect(Collectors.groupingBy(ChartAccount::getClassName,
            Collector.of(
                ClassDTO::new,
                (classDTO, chartAccount) -> {
                  classDTO.setClassName(chartAccount.getClassName());

                  classDTO.getCategories().computeIfAbsent(chartAccount.getCategory(), category -> {
                    return new CategoryDTO(chartAccount.getCategoryName(), new ArrayList<>());
                  }).getAccounts().add(new AccountDTO(chartAccount.getAccountId(), chartAccount.getAccountName(), chartAccount.isEditable()));
                },
                (left, right) -> {
                  left.getCategories().putAll(right.getCategories());
                  return left;
                }
            )
        ));

    return result;
  }

}
