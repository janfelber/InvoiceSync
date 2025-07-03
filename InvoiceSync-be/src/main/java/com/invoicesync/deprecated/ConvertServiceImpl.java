package com.invoicesync.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ConvertServiceImpl implements ConvertService {

  @Override
  public byte[] convertToPohoda(final InputStream excelInputStream) {
    final List<String> noveHeadery = List.of("Neger", "Nigga", "IvanIger");

    try (Workbook workbook = new XSSFWorkbook(excelInputStream)) {
      final Sheet sheet = workbook.getSheetAt(0);

      final Row headerRow = sheet.getRow(0);
      if (headerRow != null) {
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
          final Cell cell = headerRow.getCell(i);
          if (cell != null && i < noveHeadery.size()) {
            cell.setCellValue(noveHeadery.get(i));
          }
        }
      } else {
        System.out.println("Žiadne headery nenájdené!");
      }

      try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        workbook.write(out);
        return out.toByteArray(); // Vraciame upravený Excel ako pole bytov
      }

    } catch (Exception e) {
      throw new RuntimeException("Chyba pri spracovaní excel súboru: " + e.getMessage(), e);
    }
  }

}
