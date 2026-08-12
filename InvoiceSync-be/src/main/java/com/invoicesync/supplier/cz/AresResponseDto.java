package com.invoicesync.supplier.cz;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AresResponseDto(

    @JsonProperty("obchodniJmeno")
    String name,

    @JsonProperty("ico")
    String registrationNumber,

    @JsonProperty("dic")
    String taxId,

    @JsonProperty("icoId")
    String vatId,

    @JsonProperty("sidlo")
    AresRegisteredOffice registeredOffice

) {

}
