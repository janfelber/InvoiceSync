package com.invoicesync.emailsubscribe;

import org.springframework.stereotype.Service;

import com.invoicesync.emailsubscribe.dto.EmailSubscribeRequest;
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
