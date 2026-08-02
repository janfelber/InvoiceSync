package com.invoicesync.modules.invoice.extraction.model.dto;

import java.util.List;

public record ExtractionMeta(

    String model,

    String confidence,

    List<String> warnings

) {

}
