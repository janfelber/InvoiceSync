package com.invoicesync.emailsubscribe.dto;

import com.invoicesync.emailsubscribe.enums.EmailSubscribeType;

public record EmailSubscribeRequest(
    Long id,

    String email,

    EmailSubscribeType type
) {
}
