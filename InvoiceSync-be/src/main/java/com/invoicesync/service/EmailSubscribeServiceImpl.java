package com.invoicesync.service;

import org.springframework.stereotype.Service;

import com.invoicesync.module.EmailSubscribe;
import com.invoicesync.repository.EmailSubscribeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailSubscribeServiceImpl implements EmailSubscribeService {

  private EmailSubscribeRepository emailSubscribeRepository;

  @Override
  public EmailSubscribe createEmailSubscribe(final EmailSubscribe emailSubscribe) {
    return emailSubscribeRepository.save(emailSubscribe);
  }

}
