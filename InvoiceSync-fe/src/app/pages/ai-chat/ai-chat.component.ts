import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {AiService} from "../../core/services/ai.service";
import {LastChat} from "../../core/models/last-chats-list";
import {ChatDto} from "../../core/models/chat-dto";

@Component({
  selector: 'app-ai-chat',
  imports: [
    NgForOf,
    NgIf,
    FormsModule,
    NgClass,
  ],
  templateUrl: './ai-chat.component.html'
})
export class AiChatComponent implements OnInit {

  constructor(private aiService: AiService) {}

  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  protected lastChatsList: LastChat[] = [];
  protected messages: ChatDto[] = [];
  protected userMessage = '';
  protected isLoading = false;
  protected activeConversationId: number | null = null;
  protected historyDrawerOpen = false;

  protected suggestions: string[] = [
    'Čo je prenesená daňová povinnosť?',
    'Ako vypočítam DPH z faktúry?',
    'Aký je rozdiel medzi dobropisom a faktúrou?',
    'Kedy musím podať daňové priznanie?',
  ];

  ngOnInit(): void {
    this.loadUserConversations();
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    try {
      const el = this.scrollContainer?.nativeElement;
      if (el) el.scrollTop = el.scrollHeight;
    } catch {}
  }

  loadUserConversations(): void {
    this.aiService.getUserConversations().then((response) => {
      this.lastChatsList = response.data as LastChat[];
    });
  }

  loadConversationMessages(conversationId: number): void {
    this.activeConversationId = conversationId;
    this.aiService.getMessagesForConversation(conversationId).then((response) => {
      this.messages = response.data as ChatDto[];
    });
  }

  newChat(): void {
    this.messages = [];
    this.activeConversationId = null;
    this.userMessage = '';
  }

  sendSuggestion(text: string): void {
    this.userMessage = text;
    this.sendMessage();
  }

  onEnter(event: KeyboardEvent): void {
    if (!event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  sendMessage(): void {
    if (!this.userMessage.trim() || this.isLoading) return;

    const content = this.userMessage.trim();
    this.userMessage = '';
    this.isLoading = true;

    this.messages.push({ role: 'USER', content } as ChatDto);

    // this.aiService.sendMessage(content, this.activeConversationId).then((response) => {
    //   const reply = response.data as ChatDto;
    //   this.messages.push(reply);
    //   if (reply.conversationId) {
    //     this.activeConversationId = reply.conversationId;
    //     this.loadUserConversations();
    //   }
    // }).catch(() => {
    //   this.messages.push({ role: 'ASSISTANT', content: 'Nastala chyba. Skús to znova.' } as ChatDto);
    // }).finally(() => {
    //   this.isLoading = false;
    // });
  }
}
