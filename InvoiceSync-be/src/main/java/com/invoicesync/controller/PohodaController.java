package com.invoicesync.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.dto.invoice.InvoiceRequestDTO;
import com.invoicesync.service.InvoiceXmlService;
import com.invoicesync.xml.utils.InvoiceXmlHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pohoda")
public class PohodaController {

  private final InvoiceXmlHelper xmlHelper;

  private final InvoiceXmlService invoiceXmlService;

  @PostMapping("/export/received")
  public ResponseEntity<byte[]> createInvoice(@RequestBody final InvoiceRequestDTO invoiceRequestDTO) {
    try {
      final byte[] xmlData = invoiceXmlService.generatePohodaInvoiceXml(invoiceRequestDTO);
      System.out.println("XML data: " + new String(xmlData, StandardCharsets.UTF_8));

      final HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.xml");
      headers.add(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");

      return new ResponseEntity<>(xmlData, headers, HttpStatus.OK);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

}
