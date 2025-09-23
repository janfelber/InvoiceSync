package com.invoicesync.qrlogin;

import java.util.Optional;

public interface QRLoginService {

  String createQrToken();

  void consumeToken(QRLogin qrLogin);

  Optional<QRLogin> validateToken(String token);

}
