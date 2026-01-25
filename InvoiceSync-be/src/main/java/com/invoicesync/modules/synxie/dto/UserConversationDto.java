package com.invoicesync.modules.synxie.dto;

import java.util.UUID;

public record UserConversationDto(
    UUID conversationId,
    String title
) {

}
