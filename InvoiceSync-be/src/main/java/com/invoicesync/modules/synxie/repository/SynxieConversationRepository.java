package com.invoicesync.modules.synxie.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.invoicesync.modules.synxie.model.Conversation;

public interface SynxieConversationRepository
    extends JpaRepository<Conversation, UUID>, JpaSpecificationExecutor<Conversation> {

  Optional<Conversation> findByIdAndUserId(UUID id, String userId);

}
