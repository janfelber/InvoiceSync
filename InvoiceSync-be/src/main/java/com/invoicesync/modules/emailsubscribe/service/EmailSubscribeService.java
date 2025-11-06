package com.invoicesync.modules.emailsubscribe.service;

import com.invoicesync.modules.emailsubscribe.model.EmailSubscribeRequest;

public interface EmailSubscribeService {

  Long subscribe(EmailSubscribeRequest emailSubscribeRequest);

}
