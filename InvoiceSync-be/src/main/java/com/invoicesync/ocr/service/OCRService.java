package com.invoicesync.ocr.service;

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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    return fullText.toString();
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
      return matcher.group(1);
    }
    return "Variabilný symbol nebol nájdený.";
  }

  // VAT_ID
  public String findVatNumber(String ocrText) {
    String regex = "IČDPH:" + RegexPatterns.VAT_NUMBER;
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
      return matcher.group(0);
    }
    return "IBAN nebol nájdený.";
  }

  // ICO
  public String findIco (String ocrText) {
    String regex = "IČO:" + RegexPatterns.REGISTRATION_NUMBER;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      return matcher.group(0);
    }
    return "IČO nebol nájdený.";
  }


  // DIC
  public String findTaxNumber(final String ocrText) {
    String regex = "DIČ:" + RegexPatterns.TAX_NUMBER;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(ocrText);

    if (matcher.find()) {
      return matcher.group(0);
    }
    return "DIČ nebol nájdený.";
  }
}


