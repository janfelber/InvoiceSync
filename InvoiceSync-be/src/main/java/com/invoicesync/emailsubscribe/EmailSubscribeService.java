package com.invoicesync.emailsubscribe;

import com.invoicesync.emailsubscribe.dto.EmailSubscribeRequest;

public interface EmailSubscribeService {

  Long subscribe(EmailSubscribeRequest emailSubscribeRequest);

}
