package com.invoicesync.supplier.cz;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AresRegisteredOffice(

    @JsonProperty("nazevObce")
    String city,

    @JsonProperty("nazevUlice")
    String street,

    @JsonProperty("cisloDomovni")
    String houseNumber,

    @JsonProperty("cisloOrientacni")
    String orientationNumber,

    @JsonProperty("psc")
    String postalCode

) {

}
