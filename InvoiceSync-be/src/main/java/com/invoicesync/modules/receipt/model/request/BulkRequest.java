package com.invoicesync.modules.receipt.model.request;

import java.util.List;

public record BulkRequest(
    List<Long> ids
) {

}
