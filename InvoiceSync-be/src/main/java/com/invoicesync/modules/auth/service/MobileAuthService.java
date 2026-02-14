package com.invoicesync.modules.auth.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.modules.auth.model.MobileQrResponse;
import com.invoicesync.modules.auth.model.MobileVerifyRequest;
import com.invoicesync.modules.auth.model.MobileVerifyResponse;
import com.invoicesync.modules.user.model.User;

public interface MobileAuthService {

  MobileQrResponse generateQrSession(Authentication connectedUser);

  MobileVerifyResponse verifyAndCreateApiKey(MobileVerifyRequest request);

  User validateApiKey(String plainApiKey);

}
