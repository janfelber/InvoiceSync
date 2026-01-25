package com.invoicesync.modules.synxie.dto;

import java.util.UUID;

public record ChatRequestDto(
    UUID conversationId,
    String message
) {}
