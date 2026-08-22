package com.invoicesync.modules.auth.controller;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record VerificationRequest(

    String token

) {

}
