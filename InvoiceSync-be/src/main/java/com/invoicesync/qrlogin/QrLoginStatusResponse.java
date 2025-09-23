package com.invoicesync.qrlogin;

public record QrLoginStatusResponse (

    String status,       // PENDING | CONFIRMED | EXPIRED
    String accessToken,  // môže byť null, ak ešte nie je potvrdené
    String userId        // voliteľne, ak chceš rovno poslať info o užívateľovi
) {

}
