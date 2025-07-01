package com.invoicesync.chartaccount;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.chartaccount.dto.AccountDTO;
import com.invoicesync.chartaccount.dto.AddedAccountDTO;
import com.invoicesync.chartaccount.dto.CategoryDTO;
import com.invoicesync.chartaccount.dto.ChartAccountRequest;
import com.invoicesync.chartaccount.dto.ClassDTO;
import com.invoicesync.chartaccount.specification.ChartAccountSpecification;
import com.invoicesync.company.Company;
import com.invoicesync.company.CompanyRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChartAccountServiceImpl implements ChartAccountService {

  private final ChartAccountRepository chartAccountRepository;

  private final LegalAccountValidator legalAccountChecker;

  private final CompanyRepository companyRepository;

  private final ChartAccountMapper chartAccountMapper;

  @Override
  public List<ClassDTO> getAllChartAccountsByClass(final Long companyId) {
    final List<ChartAccount> chartAccounts = chartAccountRepository.findAll(
        ChartAccountSpecification.withCompanyId(companyId));

    final Map<String, ClassDTO> classMap = new TreeMap<>(Comparator.comparingInt(Integer::parseInt));

    for (final ChartAccount chartAccount : chartAccounts) {
      final ClassDTO classDTO = classMap.computeIfAbsent(chartAccount.getClassId(), key ->
          ClassDTO.builder()
              .classNumber(Integer.parseInt(chartAccount.getClassId()))
              .className(chartAccount.getClassName())
              .categories(new ArrayList<>())
              .build()
      );

      final List<CategoryDTO> categories = classDTO.getCategories();

      final CategoryDTO categoryDTO = categories.stream()
          .filter(cat -> cat.getCategoryId().equals(chartAccount.getCategory()))
          .findFirst()
          .orElseGet(() -> {
            final CategoryDTO newCategory = CategoryDTO.builder()
                .categoryId(chartAccount.getCategory())
                .categoryName(chartAccount.getCategoryName())
                .accounts(new ArrayList<>())
                .build();
            categories.add(newCategory);
            return newCategory;
          });

      categoryDTO.getAccounts().add(AccountDTO.builder()
          .id(chartAccount.getId())
          .number(chartAccount.getAccountId())
          .name(chartAccount.getAccountName())
          .editable(chartAccount.isEditable())
          .build());
    }

    classMap.values().forEach(classDto -> {
      classDto.getCategories().sort(Comparator.comparing(CategoryDTO::getCategoryId));
      classDto.getCategories().forEach(cat ->
          cat.getAccounts().sort(Comparator.comparing(acc -> Integer.parseInt(acc.getNumber())))
      );
    });

    return new ArrayList<>(classMap.values());
  }

  @Override
  public List<AddedAccountDTO> importExternalChartAccounts(final MultipartFile file, final Long companyId) {
    final List<AddedAccountDTO> addedAccounts = new ArrayList<>();

    try (InputStream inputStream = file.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream)) {

      final Company company = companyRepository.findById(companyId)
          .orElseThrow(() -> new IllegalArgumentException("Company with ID " + companyId + " does not exist."));

      final Sheet sheet = workbook.getSheetAt(0);
      boolean isFirstRow = true;
      int accountIdColIndex = -1;
      int accountNameColIndex = -1;

      for (final Row row : sheet) {
        if (isFirstRow) {
          for (final Cell cell : row) {
            final String header = cell.getStringCellValue().trim();

            if ("Číslo účtu".equalsIgnoreCase(header)) {
              accountIdColIndex = cell.getColumnIndex();
            }
            if ("Názov".equalsIgnoreCase(header)) {
              accountNameColIndex = cell.getColumnIndex();
            }
          }

          if (accountIdColIndex == -1 || accountNameColIndex == -1) {
            throw new RuntimeException("Stĺpce 'Číslo účtu' alebo 'Názov' sa nenašli.");
          }

          isFirstRow = false;
          continue;
        }

        final Cell accountIdCell = row.getCell(accountIdColIndex);
        final Cell accountNameCell = row.getCell(accountNameColIndex);

        final String accountId = accountIdCell != null ? accountIdCell.toString().trim() : "";
        final String accountName = accountNameCell != null ? accountNameCell.toString().trim() : "";

        if (legalAccountChecker.isLegalAccount(accountId)) {
          System.out.println("Skipping legal account: " + accountId + ", name: " + accountName);
        } else if (chartAccountRepository.existsByCompanyAndAccountId(company, accountId)) {
          System.out.println("Skipping already existing account: " + accountId + ", name: " + accountName);
        } else {
          final ChartAccount account = new ChartAccount();
          account.setCompany(company);
          account.setClassId("99");
          account.setClassName("Vlastné účty");
          account.setAccountId(accountId);
          account.setAccountName(accountName);
          account.setCategory(AccountUtils.getCategoryIdFromAccount(accountId));
          account.setCategoryName(AccountUtils.getCategoryIdFromAccount(accountId));
          account.setEditable(true);

          chartAccountRepository.save(account);
          addedAccounts.add(new AddedAccountDTO(accountId, accountName));
        }
      }

    } catch (Exception e) {
      throw new RuntimeException("An error has occured during importing", e);
    }

    return addedAccounts;
  }

  @Override
  public Long saveChartAccount(final ChartAccountRequest request) {
    System.out.println("Service Request: " + request);
    final ChartAccount chartAccount = chartAccountMapper.toChartAccount(request);
    return chartAccountRepository.save(chartAccount).getId();
  }
}
