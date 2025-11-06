package com.invoicesync.modules.emailsubscribe.service;

import org.springframework.stereotype.Service;

import com.invoicesync.core.exception.EmailAlreadySubscribedException;
import com.invoicesync.modules.emailsubscribe.mapper.EmailSubscribeMapper;
import com.invoicesync.modules.emailsubscribe.model.EmailSubscribe;
import com.invoicesync.modules.emailsubscribe.model.EmailSubscribeRequest;
import com.invoicesync.modules.emailsubscribe.repository.EmailSubscribeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailSubscribeImpl implements EmailSubscribeService {

  private final EmailSubscribeMapper emailSubscribeMapper;

  private final EmailSubscribeRepository emailSubscribeRepository;

  @Override
  public Long subscribe(final EmailSubscribeRequest emailSubscribeRequest) {
    if (emailSubscribeRepository.existsByEmail(emailSubscribeRequest.email())) {
      throw new EmailAlreadySubscribedException(
          "Email is already subscribed: " + emailSubscribeRequest.email()
      );
    }

    final EmailSubscribe subscribe = emailSubscribeMapper.toSubscribe(emailSubscribeRequest);
    return this.emailSubscribeRepository.save(subscribe).getId();
  }

}
