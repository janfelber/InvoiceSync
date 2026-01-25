package com.invoicesync.modules.synxie.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.synxie.model.Message;

public interface SynxieMessageRepository extends JpaRepository<Message, UUID> {

  List<Message> findByConversationId(UUID id);

}
