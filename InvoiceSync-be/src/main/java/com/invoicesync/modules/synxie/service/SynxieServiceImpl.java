package com.invoicesync.modules.synxie.service;

import static com.invoicesync.modules.synxie.specification.SynxieSpecification.withUserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.loader.KnowledgeLoader;
import com.invoicesync.modules.synxie.LlmClient;
import com.invoicesync.modules.synxie.dto.ChatRequestDto;
import com.invoicesync.modules.synxie.dto.ChatResponseDto;
import com.invoicesync.modules.synxie.dto.MessageDto;
import com.invoicesync.modules.synxie.dto.UserConversationDto;
import com.invoicesync.modules.synxie.model.Conversation;
import com.invoicesync.modules.synxie.model.Message;
import com.invoicesync.modules.synxie.repository.SynxieConversationRepository;
import com.invoicesync.modules.synxie.repository.SynxieMessageRepository;
import com.invoicesync.modules.synxie.role.MessageRole;

@Service
public class SynxieServiceImpl implements SynxieService {

  private final SynxieConversationRepository conversationRepository;

  private final SynxieMessageRepository messageRepository;

  private final LlmClient llmClient;

  final KnowledgeLoader knowledgeLoader;

  public SynxieServiceImpl(SynxieConversationRepository conversationRepository,
      SynxieMessageRepository messageRepository, LlmClient llmClient, final KnowledgeLoader knowledgeLoader) {
    this.conversationRepository = conversationRepository;
    this.messageRepository = messageRepository;
    this.llmClient = llmClient;
    this.knowledgeLoader = knowledgeLoader;
  }

  @Override
  public List<UserConversationDto> getUserConversations(final Authentication connectedUser) {
    List<Conversation> conversations = conversationRepository.findAll(withUserId(connectedUser.getName()));

    return conversations.stream().map(convo -> new UserConversationDto(convo.getId(), convo.getTitle())).toList();
  }

  @Override
  public List<MessageDto> getMessagesInConversation(final UUID id, final Authentication connectedUser) {
    return messageRepository.findByConversationId(id).stream()
        .map(msg -> new MessageDto(
            msg.getConversationId(),
            msg.getRole().name(),
            msg.getContent()
        ))
        .toList();
  }

  @Override
  public ChatResponseDto chat(Authentication connectedUser, ChatRequestDto request) {
    Conversation convo = getOrCreateConversation(connectedUser, request.conversationId());
    String legalContext = knowledgeLoader.loadDphKnowledge();

    String userPrompt = """
        LEGAL CONTEXT:
        ---
        %s
        ---
        
        QUESTION:
        %s
        """.formatted(legalContext, request.message());

    messageRepository.save(new Message(convo, MessageRole.USER, userPrompt, LocalDateTime.now()));

    List<Message> history = messageRepository.findAll();

    String systemPrompt = buildSystemPrompt();

    String answer = llmClient.ask(systemPrompt, history);

    messageRepository.save(new Message(convo, MessageRole.ASSISTANT, answer, LocalDateTime.now()));

    return new ChatResponseDto(convo.getId(), answer);
  }

  private Conversation getOrCreateConversation(Authentication connectedUser, UUID conversationId) {
    String userId = connectedUser.getName();

    if (conversationId == null) {
      Conversation convo = new Conversation();
      convo.setUserId(userId);
      convo.setTitle("New conversation");
      return conversationRepository.save(convo);
    }

    return conversationRepository.findByIdAndUserId(conversationId, userId)
        .orElseThrow(() -> new RuntimeException("Conversation not found"));
  }

  private String buildSystemPrompt() {

    return """
        You are a professional accounting assistant
        specialized in Slovak and Czech accounting.
        
        Rules:
        - Answer only accounting and tax questions
        - Prefer Slovak legislation
        - If unsure, say you are unsure
        - Be concise and practical
        """;
  }

}
