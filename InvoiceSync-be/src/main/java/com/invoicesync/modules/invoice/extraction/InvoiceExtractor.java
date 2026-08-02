package com.invoicesync.modules.invoice.extraction;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.invoicesync.config.ClaudeApiConfig;

@Service
public class InvoiceExtractor {

  private final HttpClient httpClient;

  private final ObjectMapper objectMapper;

  private final ClaudeApiConfig claudeApiConfig;

  public InvoiceExtractor(final ClaudeApiConfig claudeApiConfig) {
    this.objectMapper = new ObjectMapper();
    this.claudeApiConfig = claudeApiConfig;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
  }

  public String getModelName() {
    return claudeApiConfig.getModel();
  }

  public String extract(byte[] invoicePdf, ExtractionMode mode) throws Exception {
    String base64InvoicePdf = Base64.getEncoder().encodeToString(invoicePdf);
    String prompt = InvoiceExtractionPromptBuilder.buildPrompt(mode);
    int maxTokens = InvoiceExtractionPromptBuilder.recommendedMaxTokens(mode);

    String requestBody = buildRequestBody(base64InvoicePdf, prompt, maxTokens);

    HttpRequest invoiceExtractionPrompt = HttpRequest.newBuilder()
        .uri(URI.create(claudeApiConfig.getApiUrl()))
        .header("x-api-key", claudeApiConfig.getApiKey())
        .header("anthropic-version", claudeApiConfig.getAnthropicVersion())
        .header("content-type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    HttpResponse<String> response = httpClient.send(invoiceExtractionPrompt, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new Exception("Failed to extract invoice: " + response.body());
    }

    return extractTextFromResponse(response.body());
  }

  private String buildRequestBody(String base64InvoicePdf, String prompt, int maxTokens) throws Exception {
    ObjectNode root = objectMapper.createObjectNode();
    root.put("model", claudeApiConfig.getModel());
    root.put("max_tokens", maxTokens);

    ArrayNode messages = root.putArray("messages");
    ObjectNode userMessage = messages.addObject();
    userMessage.put("role", "user");

    ArrayNode content = userMessage.putArray("content");

    ObjectNode documentBlock = content.addObject();
    documentBlock.put("type", "document");
    ObjectNode source = documentBlock.putObject("source");
    source.put("type", "base64");
    source.put("media_type", "application/pdf");
    source.put("data", base64InvoicePdf);

    ObjectNode textBlock = content.addObject();
    textBlock.put("type", "text");
    textBlock.put("text", prompt);

    return objectMapper.writeValueAsString(root);

  }

  private String extractTextFromResponse(String responseBody) throws Exception {
    JsonNode root = objectMapper.readTree(responseBody);
    JsonNode contentArray = root.get("content");

    StringBuilder result = new StringBuilder();
    if (contentArray != null && contentArray.isArray()) {
      for (JsonNode block : contentArray) {
        if ("text".equals(block.path("type").asText())) {
          result.append(block.path("text").asText());
        }
      }
    }

    String text = result.toString().trim();

    // In case when model returns JSON, remove the ```json part
    if (text.startsWith("```")) {
      text = text.replaceFirst("^```json", "")
          .replaceFirst("^```", "")
          .replaceFirst("```$", "")
          .trim();
    }

    return text;
  }

}
