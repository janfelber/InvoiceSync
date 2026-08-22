package com.invoicesync.modules.auth.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record ResetPasswordRequest(

    @NotBlank
    String token,

    @NotBlank
    @Size(min = 12, max = 100)
    String newPassword

) {

}
