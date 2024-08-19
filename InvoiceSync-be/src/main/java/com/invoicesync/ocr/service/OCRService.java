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
}
