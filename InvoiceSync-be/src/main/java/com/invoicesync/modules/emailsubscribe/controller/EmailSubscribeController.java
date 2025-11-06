package com.invoicesync.modules.emailsubscribe.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.modules.emailsubscribe.model.EmailSubscribeRequest;
import com.invoicesync.modules.emailsubscribe.service.EmailSubscribeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email-subscribe")
public class EmailSubscribeController {

  private final EmailSubscribeService emailSubscribeService;

  @PostMapping("/subscribe")
  public Long subscribe(@RequestBody final EmailSubscribeRequest emailSubscribeRequest) {
    return emailSubscribeService.subscribe(emailSubscribeRequest);
  }

}
