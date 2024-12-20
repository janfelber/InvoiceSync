package com.invoicesync.ocr.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.protobuf.ByteString;
import com.invoicesync.ocr.service.regex.RegexPatterns;

@Service
public class OCRService {

  public String extractTextFromPDF(String pdfPath) throws IOException {
    StringBuilder fullText = new StringBuilder();

    // Load PDF and initialize PDFRenderer
    try (PDDocument document = PDDocument.load(new File(pdfPath))) {
      PDFRenderer pdfRenderer = new PDFRenderer(document);

      List<ByteString> imageBytesList = new ArrayList<>();
      for (int page = 0; page < document.getNumberOfPages(); ++page) {
        BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 150);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bim, "png", baos);
        baos.flush();
        imageBytesList.add(ByteString.copyFrom(baos.toByteArray()));
        baos.close();
      }

      // Prepare OCR requests
      List<AnnotateImageRequest> requests = new ArrayList<>();
      for (ByteString imgBytes : imageBytesList) {
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
            .addFeatures(feat)
            .setImage(img)
            .build();
        requests.add(request);
      }

      // Perform OCR
      try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
        BatchAnnotateImagesRequest batchRequest = BatchAnnotateImagesRequest.newBuilder()
            .addAllRequests(requests)
            .build();
        BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(batchRequest);
        List<AnnotateImageResponse> responses = batchResponse.getResponsesList();

        // Collect text from responses
        for (AnnotateImageResponse res : responses) {
          if (res.hasError()) {
            System.err.println("Error: " + res.getError().getMessage());
            return "Error occurred during OCR processing.";
          }
          TextAnnotation annotation = res.getFullTextAnnotation();
          fullText.append(annotation.getText());
        }
      }
    }

    System.out.println(fullText);

    return fullText.toString();
  }

  public String extractSupplierSection(final String ocrText) {
    final String regex = "Dodávateľ\\s*:?\\s*([\\s\\S]*?)(?=\\n\\s*Odberateľ|$)";
    final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
    final Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      String result = matcher.group(1).trim();
      result = result.replaceAll("^[':]+\\s*", "");
      return result;
    }
    return "Sekcia 'Dodávateľ' nebola nájdená.";
  }

  // INVOICE_ISSUE_DATE
  public String findIssueDate(String ocrText) {
    String regex = "Dátum vystavenia:" + RegexPatterns.DATE_DD_MM_YYYY;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      return matcher.group(1);
    }
    return "Dátum splatnosti nebol nájdený.";
  }

  public String extractSupplierName(final String supplierSection) {
    final String[] lines = supplierSection.split("\\n");
    return lines.length > 0 ? lines[0].trim() : "Názov nenájdený";
  }

  public String extractSupplierAddress(final String supplierSection) {
    final String[] lines = supplierSection.split("\\n");
    return lines.length > 1 ? lines[1].trim() : "Adresa nenájdená";
  }

  public String extractSupplierPostalCode(final String supplierSection) {
    final String regex = "\\b(\\d{3}\\s?\\d{2})\\b";
    final Pattern pattern = Pattern.compile(regex);
    final Matcher matcher = pattern.matcher(supplierSection);

    if (matcher.find()) {
      return matcher.group(1).trim();
    }
    return "PSČ nebolo nájdené.";
  }

  public String extractSupplierCity(final String supplierSection) {
    final String[] parts = supplierSection.split("\\b\\d{3}\\s?\\d{2}\\b");

    if (parts.length > 1) {
      String cityPart = parts[1].trim();
      final int endIndex = cityPart.indexOf('\n');
      if (endIndex > -1) {
        cityPart = cityPart.substring(0, endIndex).trim();
      }

      return cityPart.isEmpty() ? "Mesto nebolo nájdené." : cityPart;
    }

    return "Mesto nebolo nájdené.";
  }

  // INVOICE_DELIVERY_DATE
  public String findDeliveryDate(String ocrText) {
    String regex = "Dátum dodania:" + RegexPatterns.DATE_DD_MM_YYYY;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      return matcher.group(1);
    }
    return "Dátum dodania nebol nájdený.";
  }

  // INVOICE_DUE_DATE
  public String findDueDate(String ocrText) {
    String regex = "Dátum splatnosti:" + RegexPatterns.DATE_DD_MM_YYYY;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      return matcher.group(1);
    }
    return "Dátum splatnosti nebol nájdený.";
  }

  // VARIABLE_SYMBOL
  public String findVariableSymbol(String ocrText) {
    String regex = "Variabilný symbol:" + RegexPatterns.VARIABLE_SYMBOL;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      System.out.println("Variable symbol: " + matcher.group(1));
      return matcher.group(1);
    }
    return "Variabilný symbol nebol nájdený.";
  }

  // VAT_ID
  public String findVatId(String ocrText) {
    String regex = "IČDPH:" + RegexPatterns.VAT_ID; // Zachytí IČ DPH pre rôzne krajiny
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      return matcher.group(1); // Vráti zachytené IČ DPH
    }
    return "IČ DPH nebolo nájdené.";
  }

  // IBAN

  public String findIban (String ocrText) {
    String regex = "IBAN:" + RegexPatterns.IBAN;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      return matcher.group(1);
    }
    return "IBAN nebol nájdený.";
  }

  // ICO
  public String findIco (String ocrText) {
    String regex = "IČO:" + RegexPatterns.REGISTRATION_NUMBER;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      System.out.println(matcher.group(1));
      return matcher.group(1);
    }
    return "IČO nebol nájdený.";
  }


  // DIC
  public String findDic (final String ocrText) {
    String regex = "DIČ:" + RegexPatterns.TAX_ID;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      System.out.println(matcher.group(1));
      return matcher.group(1);
    }
    return "DIČ nebol nájdený.";
  }
}


