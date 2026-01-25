package com.invoicesync.modules.synxie.dto;

import java.util.UUID;

public record ChatResponseDto(
    UUID conversationId,
    String answer
) {}
