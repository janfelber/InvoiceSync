package com.invoicesync.modules.synxie.dto;

import java.util.UUID;

public record MessageDto(
    UUID conversationId,
    String role,
    String content
) {

}
