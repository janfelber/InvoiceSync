package com.invoicesync.modules.synxie.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.modules.synxie.dto.ChatRequestDto;
import com.invoicesync.modules.synxie.dto.ChatResponseDto;
import com.invoicesync.modules.synxie.dto.MessageDto;
import com.invoicesync.modules.synxie.dto.UserConversationDto;
import com.invoicesync.modules.synxie.service.SynxieServiceImpl;

@RestController
@RequestMapping("/synxie")
public class AiController {

  private final SynxieServiceImpl aiChatService;

  public AiController(SynxieServiceImpl aiChatService) {
    this.aiChatService = aiChatService;
  }

  @GetMapping("/conversations")
  public List<UserConversationDto> getUserConversations(Authentication connectedUser) {
    return aiChatService.getUserConversations(connectedUser);
  }

  @GetMapping("/conversation/{id}")
  public List<MessageDto> getMessagesInConversation(@PathVariable UUID id, Authentication connectedUser) {
    return aiChatService.getMessagesInConversation(id, connectedUser);
  }

  @PostMapping("/chat")
  public ChatResponseDto chat(
      @RequestBody ChatRequestDto request,
      Authentication connectedUser
  ) {
    return aiChatService.chat(connectedUser, request);
  }

}
