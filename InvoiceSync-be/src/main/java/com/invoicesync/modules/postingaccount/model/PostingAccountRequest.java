package com.invoicesync.modules.postingaccount.model;

import com.invoicesync.core.enums.PostingAccountType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PostingAccountRequest(

    Long companyId,

    @NotNull(message = "101")
    @NotEmpty(message = "101")
    String accountId,

    @NotNull(message = "102")
    @NotEmpty(message = "102")
    String accountName,

    @NotNull(message = "103")
    PostingAccountType type

) {

}
