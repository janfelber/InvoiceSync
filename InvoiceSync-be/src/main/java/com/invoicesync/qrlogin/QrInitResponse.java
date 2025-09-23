package com.invoicesync.qrlogin;

public record QrInitResponse(
    String qrToken,
    long expiresInSeconds)
{

}
