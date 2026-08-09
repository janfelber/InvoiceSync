package com.invoicesync.modules.receipt.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.modules.invoice.model.InvoiceRequestDTO;
import com.invoicesync.modules.invoice.pohoda.service.PohodaXmlService;
import com.invoicesync.modules.xml.utils.XmlHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pohoda")
public class PohodaController {

  private final XmlHelper xmlHelper;

  private final PohodaXmlService pohodaXmlService;

  @PostMapping("/export/received")
  public ResponseEntity<byte[]> createInvoice(@RequestBody final InvoiceRequestDTO invoiceRequestDTO) {
    try {
      final byte[] xmlData = pohodaXmlService.generatePohodaInvoiceXml(invoiceRequestDTO);

      final HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.xml");
      headers.add(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");

      return new ResponseEntity<>(xmlData, headers, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();

      // Vráti detail chyby do odpovede
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(("Chyba pri generovaní faktúry: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
    }
  }

  // @PostMapping("/export/receipt")
  // public ResponseEntity<byte[]> createReceipt(@RequestBody final ReceiptRequest receiptRequest) {
  //   try {
  //     final byte[] excelData = invoiceXmlService.generatePohodaReceiptExcel(receiptRequest);
  //
  //     final HttpHeaders headers = new HttpHeaders();
  //     headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt.xlsx");
  //     headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
  //
  //     return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
  //   } catch (Exception e) {
  //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
  //   }
  // }

}
