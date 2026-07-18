package com.invoicesync.shared;

import lombok.Builder;

@Builder
public record AccountingAssignment(

    String accountValue,

    String accountText

) {

}
