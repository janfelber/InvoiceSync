package com.invoicesync.chartaccount;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.invoicesync.chartaccount.dto.AccountDTO;
import com.invoicesync.chartaccount.dto.CategoryDTO;
import com.invoicesync.chartaccount.dto.ClassDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChartAccountServiceImpl implements ChartAccountService {

  private final ChartAccountRepository chartAccountRepository;

  @Override
  public List<ClassDTO> getAllChartAccountsByClass() {
    final List<ChartAccount> chartAccounts = chartAccountRepository.findAll();

    // Použijeme mapy na zoskupenie dát
    final Map<String, ClassDTO> classMap = new LinkedHashMap<>();

    for (final ChartAccount chartAccount : chartAccounts) {
      // Získaj alebo vytvor ClassDTO
      final ClassDTO classDTO = classMap.computeIfAbsent(chartAccount.getClassId(), key ->
          ClassDTO.builder()
              .classNumber(Integer.parseInt(chartAccount.getClassId()))
              .className(chartAccount.getClassName())
              .categories(new ArrayList<>())
              .build()
      );

      final CategoryDTO categoryDTO = classDTO.getCategories().stream()
          .filter(cat -> cat.getCategoryId().equals(chartAccount.getCategory()))
          .findFirst()
          .orElseGet(() -> {
            final CategoryDTO newCategory = CategoryDTO.builder()
                .categoryId(chartAccount.getCategory())
                .categoryName(chartAccount.getCategoryName())
                .accounts(new ArrayList<>())
                .build();
            classDTO.getCategories().add(newCategory);
            return newCategory;
          });

      categoryDTO.getAccounts().add(AccountDTO.builder()
          .id(chartAccount.getId())
          .number(chartAccount.getAccountId())
          .name(chartAccount.getAccountName())
          .editable(chartAccount.isEditable())
          .build());
    }

    return new ArrayList<>(classMap.values());
  }

}
