import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {RouterLink} from "@angular/router";
import {NgClass, NgForOf, NgIf, NgSwitch, NgSwitchCase} from "@angular/common";
import {FormBuilder, FormsModule} from "@angular/forms";
import {initFlowbite} from "flowbite";
import {AiService} from "../../core/services/ai.service";
import {LastChat } from "../../core/models/last-chats-list";
import {ChatDto} from "../../core/models/chat-dto";

interface ChatMessage {
  from: 'user' | 'ai';
  text: string;
}

@Component({
  selector: 'app-ai-chat',
  imports: [
    RouterLink,
    NgClass,
    FormsModule,
    NgForOf,
    NgSwitch,
    NgSwitchCase,
    NgIf
  ],
  templateUrl: './ai-chat.component.html',
  styleUrl: './ai-chat.component.css'
})
export class AiChatComponent implements OnInit{
  constructor(
    private aiService: AiService,
  ) {

  }

  ngOnInit(): void {
    this.loadUserConversations();
      initFlowbite();
  }
  @ViewChild('chatContainer') private chatContainer!: ElementRef;
  chatsOpen = false;

  currentMessage = '';
  // messages: ChatMessage[] = [
  //   { from: 'ai', text: 'Ahoj! Ja som tvoj AI asistent.' },
  //   { from: 'user', text: 'Ahoj, ako sa máš?' },
  //   { from: 'ai', text: 'Mám sa dobre, ďakujem! Čím ti môžem pomôcť?' },
  // ];

  protected lastChatsList: LastChat[] = [];
  protected messages: ChatDto[] = [];

  // sendMessage() {
  //   if (!this.currentMessage.trim()) return;
  //
  //   this.messages.push({ from: 'user', text: this.currentMessage });
  //
  //   const userMsg = this.currentMessage;
  //   this.currentMessage = '';
  //
  //   // fake AI odpoveď
  //   setTimeout(() => {
  //     const fakeReplies = [
  //       'Zaujímavá otázka!',
  //       'To si musíš overiť.',
  //       'Samozrejme, viem ti s tým pomôcť.',
  //       'Môžeš skúsiť toto riešenie.'
  //     ];
  //     const reply = fakeReplies[Math.floor(Math.random() * fakeReplies.length)];
  //     this.messages.push({ from: 'ai', text: reply });
  //     this.scrollToBottom();
  //   }, 500);
  //
  //   this.scrollToBottom();
  // }

  clearChat() {
    this.messages = [];
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    try {
      this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
    } catch {}
  }

  loadUserConversations() {
    this.aiService.getUserConversations().then((response) => {
      this.lastChatsList = response.data as LastChat[];
      console.log(this.lastChatsList);
    })
  }

  loadConversationMessages(conversationId: number) {
    this.aiService.getMessagesForConversation(
      conversationId
    ).then((response) => {
      this.messages = response.data as ChatDto[];
      console.log(response);
    })
  }

  openChatsList() {
    this.chatsOpen = true;
  }

  closeChatsList() {
    this.chatsOpen = false;
  }
}
