package com.invoicesync.chartaccount.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ChartAccountRequest(

    Long companyId,

    @NotNull(message = "101")
    @NotEmpty(message = "101")
    String accountId,


    @NotNull(message = "102")
    @NotEmpty(message = "102")
    String accountName
    ) {

}
