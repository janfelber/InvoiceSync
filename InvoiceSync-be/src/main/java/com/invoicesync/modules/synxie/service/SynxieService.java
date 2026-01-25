package com.invoicesync.modules.synxie.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.invoicesync.modules.synxie.dto.ChatRequestDto;
import com.invoicesync.modules.synxie.dto.ChatResponseDto;
import com.invoicesync.modules.synxie.dto.MessageDto;
import com.invoicesync.modules.synxie.dto.UserConversationDto;

public interface SynxieService {

  List<UserConversationDto> getUserConversations(Authentication connectedUser);

  List<MessageDto> getMessagesInConversation(UUID id, Authentication connectedUser);

  ChatResponseDto chat(Authentication connectedUser, ChatRequestDto request);

}
