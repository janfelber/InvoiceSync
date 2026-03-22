package com.invoicesync.modules.receipt.model.request;

import java.util.List;

import com.invoicesync.core.common.BulkActionRequest;

public record BulkDeleteRequest(
    List<Long> ids
)
    implements BulkActionRequest {

}
