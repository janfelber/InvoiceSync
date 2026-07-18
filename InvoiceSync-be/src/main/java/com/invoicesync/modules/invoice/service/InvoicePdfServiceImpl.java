package com.invoicesync.modules.invoice.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.invoicesync.modules.invoice.model.InvoiceResponse;
import com.invoicesync.shared.AccountingLineItem;
import com.invoicesync.shared.MonetaryAmount;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoicePdfServiceImpl implements InvoicePdfService {

  private final TemplateEngine templateEngine;

  @Override
  public byte[] generateInvoicePdf(final InvoiceResponse invoiceId) {
    // 1. Calculate totals
    BigDecimal subtotal = invoiceId.getItems().stream()
        .map(AccountingLineItem::totalPrice)
        .filter(Objects::nonNull)
        .map(MonetaryAmount::priceWithoutVAT)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal total = invoiceId.getItems().stream()
        .map(AccountingLineItem::totalPrice)
        .filter(Objects::nonNull)
        .map(MonetaryAmount::priceWithVAT)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal tax = total.subtract(subtotal);

    // 2. Build Thymeleaf context
    Context context = new Context();
    context.setVariable("invoice", invoiceId);
    context.setVariable("subtotal", subtotal);
    context.setVariable("tax", tax);
    context.setVariable("total", total);

    // 3. Process template → HTML string
    String html = templateEngine.process("invoice", context);

    // 4. Convert HTML → PDF bytes
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ITextRenderer renderer = new ITextRenderer();
      renderer.getFontResolver().addFont("fonts/LiberationSans-Regular.ttf",
          com.lowagie.text.pdf.BaseFont.IDENTITY_H, com.lowagie.text.pdf.BaseFont.EMBEDDED);
      renderer.getFontResolver().addFont("fonts/LiberationSans-Bold.ttf",
          com.lowagie.text.pdf.BaseFont.IDENTITY_H, com.lowagie.text.pdf.BaseFont.EMBEDDED);
      renderer.setDocumentFromString(html);
      renderer.layout();
      renderer.createPDF(out);
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate PDF", e);
    }
  }

}
