package com.invoicesync.modules.postingaccount.service;

import static com.invoicesync.modules.postingaccount.specification.PostingAccountSpecification.cashDocumentsCompanyId;
import static com.invoicesync.modules.postingaccount.specification.PostingAccountSpecification.internalDocumentsCompanyId;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.enums.PostingAccountType;
import com.invoicesync.core.utils.AccountUtils;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.company.repository.CompanyRepository;
import com.invoicesync.modules.postingaccount.documents.cash.CashDocumentAccount;
import com.invoicesync.modules.postingaccount.documents.cash.CashDocumentAccountRepository;
import com.invoicesync.modules.postingaccount.documents.internal.InternalDocumentAccount;
import com.invoicesync.modules.postingaccount.documents.internal.InternalDocumentAccountRepository;
import com.invoicesync.modules.postingaccount.mapper.PostingAccountMapper;
import com.invoicesync.modules.postingaccount.model.AccountDTO;
import com.invoicesync.modules.postingaccount.model.AddedAccountDTO;
import com.invoicesync.modules.postingaccount.model.BaseDocumentAccountEntity;
import com.invoicesync.modules.postingaccount.model.CategoryDTO;
import com.invoicesync.modules.postingaccount.model.ClassDTO;
import com.invoicesync.modules.postingaccount.model.PostingAccountRequest;
import com.invoicesync.modules.postingaccount.model.PostingAccountResponse;
import com.invoicesync.modules.postingaccount.validator.LegalAccountValidator;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostingAccountServiceImpl implements PostingAccountService {

  private final CompanyRepository companyRepository;

  private final InternalDocumentAccountRepository internalDocumentAccountRepository;

  private final CashDocumentAccountRepository cashDocumentAccountRepository;

  private final LegalAccountValidator legalAccountChecker;

  private final PostingAccountMapper postingAccountMapper;

  @Override
  public List<ClassDTO> getAvailableAccount(final Long companyId, final PostingAccountType type,
      final Authentication connectedUser) {
    final Company company = companyRepository.findById(companyId).orElseThrow();

    if (!company.getCreatedBy().equals(connectedUser.getName())) {
      throw new AccessDeniedException("You cannot access accounts of this company");
    }

    final List<? extends BaseDocumentAccountEntity> accountEntities = switch (type) {
      case INTERNAL -> internalDocumentAccountRepository.findAll(
          internalDocumentsCompanyId(companyId));
      case CASH -> cashDocumentAccountRepository.findAll(cashDocumentsCompanyId(companyId));
    };

    final Map<String, ClassDTO> classMap = new TreeMap<>(Comparator.comparingInt(Integer::parseInt));

    for (final BaseDocumentAccountEntity account : accountEntities) {
      final ClassDTO classDTO = classMap.computeIfAbsent(account.getClassId(), key ->
          ClassDTO.builder()
              .classNumber(Integer.parseInt(account.getClassId()))
              .className(account.getClassName())
              .categories(new ArrayList<>())
              .build()
      );

      final List<CategoryDTO> categories = classDTO.getCategories();

      final CategoryDTO categoryDTO = categories.stream()
          .filter(cat -> cat.getCategoryId().equals(account.getCategory()))
          .findFirst()
          .orElseGet(() -> {
            final CategoryDTO newCategory = CategoryDTO.builder()
                .categoryId(account.getCategory())
                .categoryName(account.getCategoryName())
                .accounts(new ArrayList<>())
                .build();
            categories.add(newCategory);
            return newCategory;
          });

      categoryDTO.getAccounts().add(AccountDTO.builder()
          .id(account.getId())
          .number(account.getAccountId())
          .name(account.getAccountName())
          .editable(account.isEditable())
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
  public PageResponse<PostingAccountResponse> getAllPostingAccounts(final int page, final int size,
      final PostingAccountType type, final Long companyId, final Authentication connectedUser) {
    final Pageable pageable = PageRequest.of(page, size);

    Page.empty(pageable);
    final Page<? extends BaseDocumentAccountEntity> accounts = switch (type) {
      case INTERNAL -> internalDocumentAccountRepository.findAll(internalDocumentsCompanyId(companyId), pageable);
      case CASH -> cashDocumentAccountRepository.findAll(cashDocumentsCompanyId(companyId), pageable);
    };

    final List<PostingAccountResponse> postingAccountResponse = accounts.stream()
        .map(postingAccountMapper::toPostingAccountTableResponse)
        .toList();

    return new PageResponse<>(
        postingAccountResponse,
        accounts.getNumber(),
        accounts.getSize(),
        accounts.getTotalElements(),
        accounts.getTotalPages(),
        accounts.isFirst(),
        accounts.isLast()
    );
  }

  @Override
  public List<AddedAccountDTO> importExternalPostingAccounts(final MultipartFile file, final Long companyId) {
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
        } else if (internalDocumentAccountRepository.existsByCompanyAndAccountId(company, accountId)) {
          System.out.println("Skipping already existing account: " + accountId + ", name: " + accountName);
        } else {
          final InternalDocumentAccount account = new InternalDocumentAccount();
          account.setCompany(company);
          account.setClassId("99");
          account.setClassName("Vlastné účty");
          account.setAccountId(accountId);
          account.setAccountName(accountName);
          account.setCategory(AccountUtils.getCategoryIdFromAccount(accountId));
          account.setCategoryName(AccountUtils.getCategoryIdFromAccount(accountId));
          account.setEditable(true);

          internalDocumentAccountRepository.save(account);
          addedAccounts.add(new AddedAccountDTO(accountId, accountName));
        }
      }

    } catch (Exception e) {
      throw new RuntimeException("An error has occured during importing", e);
    }

    return addedAccounts;
  }

  @Override
  public Long savePostingAccount(final PostingAccountRequest request) {
    final BaseDocumentAccountEntity account = postingAccountMapper.toPostingAccount(request);

    return switch (request.type()) {
      case CASH -> cashDocumentAccountRepository.save((CashDocumentAccount) account).getId();
      case INTERNAL -> internalDocumentAccountRepository.save((InternalDocumentAccount) account).getId();
    };
  }

  @Override
  public void deletePostingAccount(final Long accountId, final Authentication connectedUser) {
    final InternalDocumentAccount internalDocumentAccount = internalDocumentAccountRepository.findById(accountId)
        .orElseThrow();

    if (!internalDocumentAccount.getCompany().getCreatedBy().equals(connectedUser.getName())) {
      throw new AccessDeniedException("You cannot delete accounts of this company");
    }

    internalDocumentAccountRepository.delete(internalDocumentAccount);
  }

}
