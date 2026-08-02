package com.invoicesync.modules.invoice.extraction.model.dto;

public record ExtractInvoiceResponse(

    String status,

    ExtractedInvoiceData invoiceData,

    ExtractionMeta metaInfo

) {

}
