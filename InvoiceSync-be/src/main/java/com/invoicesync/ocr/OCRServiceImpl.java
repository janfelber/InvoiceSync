package com.invoicesync.ocr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import com.invoicesync.subscription.UserService;
import com.invoicesync.subscription.enums.LimitType;
import com.invoicesync.subscription.guard.LimitGuardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OCRServiceImpl implements OCRService {

  private final LimitGuardService limitGuardService;

  private final UserService userService;

  public String extractTextFromPDF(final MultipartFile file, final Authentication connectedUser) {
    try (PDDocument document = PDDocument.load(file.getInputStream())) {
      final List<BufferedImage> images = renderPdfToImages(document);
      final List<ByteString> imageBytes = convertImagesToByteStrings(images);
      final String extractedText = runOcr(imageBytes);
      userService.incrementUsed(connectedUser, LimitType.INVOICE_PROCESS);
      return extractedText;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private List<BufferedImage> renderPdfToImages(final PDDocument document) throws IOException {
    final PDFRenderer renderer = new PDFRenderer(document);
    final List<BufferedImage> images = new ArrayList<>();
    for (int i = 0; i < document.getNumberOfPages(); i++) {
      images.add(renderer.renderImageWithDPI(i, 150));
    }
    return images;
  }

  private List<ByteString> convertImagesToByteStrings(final List<BufferedImage> images) throws IOException {
    final List<ByteString> result = new ArrayList<>();
    for (final BufferedImage image : images) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        ImageIO.write(image, "png", baos);
        baos.flush();
        result.add(ByteString.copyFrom(baos.toByteArray()));
      }
    }
    return result;
  }

  private String runOcr(final List<ByteString> imageBytesList) throws IOException {
    final StringBuilder fullText = new StringBuilder();

    final List<AnnotateImageRequest> requests = new ArrayList<>();
    for (final ByteString imgBytes : imageBytesList) {
      final Image img = Image.newBuilder().setContent(imgBytes).build();
      final Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
      final AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
          .addFeatures(feat)
          .setImage(img)
          .build();
      requests.add(request);
    }

    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      final BatchAnnotateImagesResponse response = client.batchAnnotateImages(
          BatchAnnotateImagesRequest.newBuilder().addAllRequests(requests).build());

      for (final AnnotateImageResponse res : response.getResponsesList()) {
        if (res.hasError()) {
          System.err.println("OCR error: " + res.getError().getMessage());
          continue;
        }
        fullText.append(res.getFullTextAnnotation().getText());
      }
    }

    return fullText.toString();
  }

}


