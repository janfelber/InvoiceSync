package com.invoicesync.modules.synxie.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.invoicesync.modules.synxie.role.MessageRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "message", schema = "invoice_sync")
public class Message {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID", updatable = false, nullable = false)
  UUID id;

  @Column(name = "conversation_id", nullable = false)
  private UUID conversationId;

  @Enumerated(EnumType.STRING)
  MessageRole role;

  @Column(nullable = false, columnDefinition = "TEXT")
  String content;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  public Message(final Conversation convo, final MessageRole messageRole, final String message,
      final LocalDateTime createdAt) {
    this.conversationId = convo.getId();
    this.role = messageRole;
    this.content = message;
    this.createdAt = createdAt;
  }

}
