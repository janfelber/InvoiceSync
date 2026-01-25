package com.invoicesync.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

@Component
public class KnowledgeLoader {

  public String loadDphKnowledge() {
    try (InputStream is =
        getClass().getResourceAsStream("/knowledge/dph.md")) {

      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot load DPH knowledge", e);
    }
  }

}
