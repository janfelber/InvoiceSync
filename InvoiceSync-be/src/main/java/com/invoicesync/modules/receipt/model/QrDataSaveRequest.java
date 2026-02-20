package com.invoicesync.modules.receipt.model;

public record QrDataSaveRequest(
    String qrData,
    Long companyId
) {
}
