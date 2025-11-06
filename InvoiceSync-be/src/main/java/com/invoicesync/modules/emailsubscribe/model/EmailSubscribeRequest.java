package com.invoicesync.modules.emailsubscribe.model;

import com.invoicesync.core.enums.EmailSubscribeType;

public record EmailSubscribeRequest(
    Long id,

    String email,

    EmailSubscribeType type
) {
}
