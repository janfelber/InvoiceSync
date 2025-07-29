package com.invoicesync.convert;

import static com.invoicesync.convert.ConvertSpecification.withUserId;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.invoicesync.convert.dto.ConvertResponseDto;
import com.invoicesync.shared.common.PageResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConvertServiceImpl implements ConvertService {

  private final ConvertRepository convertRepository;

  private final ConvertMapper convertMapper;

  @Override
  public PageResponse<ConvertResponseDto> findImportByUser(final int page, final int size,
      final Authentication connectedUser) {
    final Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    final Page<Convert> converts = convertRepository.findAll(withUserId(connectedUser.getName()), pageable);

    final List<ConvertResponseDto> convertResponse = converts.stream()
        .map(convertMapper::toConvertTableResponse)
        .toList();

    return new PageResponse<>(
        convertResponse,
        converts.getNumber(),
        converts.getSize(),
        converts.getTotalElements(),
        converts.getTotalPages(),
        converts.isFirst(),
        converts.isLast()
    );
  }

  @Override
  public Long saveConvert(final MultipartFile file, final Authentication connectedUser) {
    try (InputStream inputStream = file.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream)) {
      final Sheet sheet = workbook.getSheetAt(0);
      final Row headerRow = sheet.getRow(0);

      if (headerRow != null) {
        final Convert convert = new Convert();
        convert.setFileName(file.getOriginalFilename());

        final List<ConvertMapping> mappings = new ArrayList<>();
        final List<LinkedHashMap<String, String>> excelData = new ArrayList<>();

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
          final Cell cell = headerRow.getCell(i);
          if (cell == null) {
            continue;
          }

          final String headerName = cell.getStringCellValue();

          final ConvertMapping mapping = new ConvertMapping();
          mapping.setConvert(convert);
          mapping.setOriginalHeader(headerName);
          mapping.setIncluded(true);
          mappings.add(mapping);
        }

        // Čítanie dátových riadkov
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
          final Row row = sheet.getRow(i);
          if (row == null) {
            continue;
          }

          final LinkedHashMap<String, String> rowData = new LinkedHashMap<>();
          for (int j = 0; j < headerRow.getLastCellNum(); j++) {
            final String header = headerRow.getCell(j).getStringCellValue();
            final Cell cell = row.getCell(j);
            rowData.put(header, cell != null ? getCellAsString(cell) : "");
          }
          excelData.add(rowData);
        }

        // Premena dát na JSON
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false);
        final String jsonData = objectMapper.writeValueAsString(excelData);
        System.out.println("jsonData: " + jsonData);
        convert.setData(jsonData);
        convert.setMappings(mappings);
        convert.setStatus("UNPROCESSED");

        System.out.println("user: " + connectedUser.getName());
        return convertRepository.save(convert).getId();
      } else {
        throw new IllegalArgumentException("Excel súbor nemá hlavičkový riadok (riadok 0 je prázdny)");
      }
    } catch (Exception e) {
      throw new RuntimeException("Chyba pri spracovaní excel súboru: " + e.getMessage(), e);
    }
  }

  @Override
  public ConvertResponseDto findById(final Long convertId) {
    return convertRepository.findById(convertId)
        .map(convertMapper::toConvertResponse)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Convert with ID " + convertId + " not found."));
  }

  private String getCellAsString(final Cell cell) {
    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> String.valueOf(cell.getNumericCellValue());
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case FORMULA -> cell.getCellFormula();
      default -> "";
    };
  }

}
