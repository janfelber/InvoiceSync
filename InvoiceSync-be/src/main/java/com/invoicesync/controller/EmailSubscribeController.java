package com.invoicesync.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.module.EmailSubscribe;
import com.invoicesync.service.EmailSubscribeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email/subscribe")
public class EmailSubscribeController {

  private final EmailSubscribeService emailSubscribeService;

  @PostMapping("/news")
  public EmailSubscribe create(@RequestBody final EmailSubscribe emailSubscribe) {
    return emailSubscribeService.createEmailSubscribe(emailSubscribe);
  }

}
