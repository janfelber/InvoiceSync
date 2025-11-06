package com.invoicesync.modules.emailsubscribe.mapper;

import org.springframework.stereotype.Service;

import com.invoicesync.modules.emailsubscribe.model.EmailSubscribe;
import com.invoicesync.modules.emailsubscribe.model.EmailSubscribeRequest;

@Service
public class EmailSubscribeMapper {

  public EmailSubscribe toSubscribe(final EmailSubscribeRequest request) {
    return EmailSubscribe.builder()
        .id(request.id())
        .email(request.email())
        .emailSubscribeType(request.type())
        .build();
  }

}
