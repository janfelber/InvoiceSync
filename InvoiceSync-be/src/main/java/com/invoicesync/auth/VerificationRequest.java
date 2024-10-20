package com.invoicesync.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationRequest {
    private String login;
    private String code;
}
