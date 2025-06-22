package com.invoicesync.emailsubscribe;

import org.springframework.stereotype.Service;

import com.invoicesync.emailsubscribe.dto.EmailSubscribeRequest;
import com.invoicesync.shared.exception.EmailAlreadySubscribedException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailSubscribeImpl implements EmailSubscribeService{

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
