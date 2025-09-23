package com.invoicesync.qrlogin;

public record QrLoginConfirmRequest(
    String token,      // QR token, ktorý mobil naskenoval
    String userId,     // užívateľ ktorý sa prihlasuje (alebo jeho Keycloak ID)
    String accessToken // voliteľné - ak už máš prístupový token z Keycloaku
) {}
