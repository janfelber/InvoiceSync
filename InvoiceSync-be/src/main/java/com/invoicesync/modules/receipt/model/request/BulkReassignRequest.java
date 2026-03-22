package com.invoicesync.modules.receipt.model.request;

import java.util.List;

import com.invoicesync.core.common.BulkActionRequest;

public record BulkReassignRequest(
    List<Long> ids,
    Long companyId
)

    implements BulkActionRequest {

}
