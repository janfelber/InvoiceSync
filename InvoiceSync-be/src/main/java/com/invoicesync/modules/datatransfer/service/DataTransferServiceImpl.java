package com.invoicesync.modules.datatransfer.service;

import static com.invoicesync.modules.datatransfer.DataTransferSpecification.withUserId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.datatransfer.repository.DataTransferHeaderMappingRepository;
import com.invoicesync.modules.datatransfer.mapper.DataTransferMapper;
import com.invoicesync.modules.datatransfer.repository.DataTransferRepository;
import com.invoicesync.modules.datatransfer.dto.DataTransferHeaderMappingDto;
import com.invoicesync.modules.datatransfer.dto.DataTransferResponseDto;
import com.invoicesync.modules.datatransfer.model.DataTransfer;
import com.invoicesync.modules.datatransfer.model.DataTransferHeader;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DataTransferServiceImpl implements DataTransferService {

  private final DataTransferRepository dataTransferRepository;

  private final DataTransferMapper dataTransferMapper;

  private final DataTransferHeaderMappingRepository convertMappingRepository;

  @Override
  public PageResponse<DataTransferResponseDto> findImportByUser(final int page, final int size,
      final Authentication connectedUser) {
    final Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    final Page<DataTransfer> converts = dataTransferRepository.findAll(withUserId(connectedUser.getName()), pageable);

    final List<DataTransferResponseDto> convertResponse = converts.stream()
        .map(dataTransferMapper::toConvertTableResponse)
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
        final DataTransfer dataTransfer = new DataTransfer();
        dataTransfer.setFileName(file.getOriginalFilename());

        final List<DataTransferHeader> mappings = new ArrayList<>();
        final List<LinkedHashMap<String, String>> excelData = new ArrayList<>();

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
          final Cell cell = headerRow.getCell(i);
          if (cell == null) {
            continue;
          }

          final String headerName = cell.getStringCellValue();

          final DataTransferHeader mapping = new DataTransferHeader();
          mapping.setDataTransfer(dataTransfer);
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
        dataTransfer.setData(jsonData);
        dataTransfer.setMappings(mappings);
        dataTransfer.setStatus("UNPROCESSED");

        System.out.println("user: " + connectedUser.getName());
        return dataTransferRepository.save(dataTransfer).getId();
      } else {
        throw new IllegalArgumentException("Excel súbor nemá hlavičkový riadok (riadok 0 je prázdny)");
      }
    } catch (Exception e) {
      throw new RuntimeException("Chyba pri spracovaní excel súboru: " + e.getMessage(), e);
    }
  }

  @Override
  public DataTransferResponseDto findById(final Long convertId) {
    return dataTransferRepository.findById(convertId)
        .map(dataTransferMapper::toConvertResponse)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Convert with ID " + convertId + " not found."));
  }

  @Override
  public void saveMapping(final Long convertId, final List<DataTransferHeaderMappingDto> mappings,
      final Authentication connectedUser) {
    final DataTransfer dataTransfer = dataTransferRepository.findById(convertId).orElseThrow();

    for (final DataTransferHeaderMappingDto dto : mappings) {
      final DataTransferHeader mapping = convertMappingRepository.findById(dto.getId())
          .orElseThrow(() -> new IllegalArgumentException("Mapping not found with id " + dto.getId()));
      mapping.setMappedHeader(dto.getMappedHeader());
      mapping.setIncluded(dto.isIncluded());
    }

    final boolean allMapped = mappings.stream()
        .filter(DataTransferHeaderMappingDto::isIncluded)
        .allMatch(header -> header.getMappedHeader() != null && !header.getMappedHeader().trim().isEmpty());

    if (allMapped) {
      dataTransfer.setStatus("PROCESSED");
    }

    dataTransfer.setMappings(dataTransfer.getMappings());
    dataTransferRepository.save(dataTransfer);
  }

  @Override
  public byte[] downloadConvert(final Long convertId, final Authentication connectedUser) {
    final DataTransfer dataTransfer = dataTransferRepository.findById(convertId).orElseThrow();

    final ObjectMapper objectMapper = new ObjectMapper();
    final List<LinkedHashMap<String, String>> originalData;
    try {
      originalData = objectMapper.readValue(dataTransfer.getData(), new TypeReference<>() {
      });
    } catch (Exception e) {
      throw new RuntimeException("Nepodarilo sa načítať dáta", e);
    }

    final Map<String, String> headerMap = dataTransfer.getMappings().stream()
        .filter(DataTransferHeader::isIncluded)
        .collect(Collectors.toMap(DataTransferHeader::getOriginalHeader, mapping -> {
          final String mapped = mapping.getMappedHeader();
          return mapped != null && !mapped.isBlank() ? mapped : mapping.getOriginalHeader();
        }));

    try (Workbook workbook = new XSSFWorkbook()) {
      final Sheet sheet = workbook.createSheet(dataTransfer.getFileName());
      final CellStyle textStyle = workbook.createCellStyle();
      textStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("@"));

      // Header row
      final Row headerRow = sheet.createRow(0);
      int colIndex = 0;
      for (final String originalHeader : originalData.get(0).keySet()) {
        if (!headerMap.containsKey(originalHeader)) {
          continue;
        }
        final String mappedHeader = headerMap.get(originalHeader);
        headerRow.createCell(colIndex++).setCellValue(mappedHeader);
      }

      // Data rows
      for (int i = 0; i < originalData.size(); i++) {
        final Row row = sheet.createRow(i + 1);
        final Map<String, String> rowData = originalData.get(i);
        int j = 0;
        for (final String originalHeader : rowData.keySet()) {
          if (!headerMap.containsKey(originalHeader)) {
            continue;
          }

          final Cell cell = row.createCell(j++);
          final String value = rowData.get(originalHeader);
          cell.setCellValue(value != null ? value : "");
          cell.setCellStyle(textStyle);
        }
      }

      // Write workbook to byte array
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);

      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Chyba pri vytváraní Excelu", e);
    }
  }

  private String getCellAsString(final Cell cell) {
    final DataFormatter formatter = new DataFormatter();
    return formatter.formatCellValue(cell);
  }

}
