package com.invoicesync.modules.synxie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.invoicesync.modules.synxie.model.Message;

@Component
public class LlmClient {

  private final WebClient webClient;

  public LlmClient(@Qualifier("openAiWebClient") WebClient webClient) {
    this.webClient = webClient;
  }

  public String ask(String systemPrompt, List<Message> messages) {

    List<Map<String, String>> payloadMessages = new ArrayList<>();

    payloadMessages.add(Map.of(
        "role", "system",
        "content", systemPrompt
    ));

    for (Message message : messages) {
      payloadMessages.add(Map.of(
          "role", message.getRole().name().toLowerCase(),
          "content", message.getContent()
      ));
    }

    Map<String, Object> body = Map.of(
        "model", "gpt-4o-mini",
        "messages", payloadMessages,
        "temperature", 0.2
    );

    final String response = webClient.post()
        .uri("/chat/completions")
        .bodyValue(body)
        .retrieve()
        .bodyToMono(JsonNode.class)
        .map(node -> node.path("choices")
            .get(0)
            .path("message")
            .path("content")
            .asText())
        .block();

    return response
        .replaceAll("^```json\\s*", "")
        .replaceAll("```$", "")
        .trim();
  }

}
