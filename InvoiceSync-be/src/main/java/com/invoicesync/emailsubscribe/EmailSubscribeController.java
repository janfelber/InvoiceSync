package com.invoicesync.emailsubscribe;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.emailsubscribe.dto.EmailSubscribeRequest;

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
