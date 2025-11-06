package com.invoicesync.modules.xmlFile.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record XmlFileRequest(

    @NotNull(message = "100")
    @NotEmpty(message = "100")
    String fileName,
    @NotNull(message = "101")
    @NotEmpty(message = "101")
    String xmlContent
) {

}
