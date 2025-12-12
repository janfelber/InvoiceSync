import {Component, ElementRef, ViewChild} from '@angular/core';
import {RouterLink} from "@angular/router";
import {NgClass} from "@angular/common";
import {FormsModule} from "@angular/forms";

interface ChatMessage {
  from: 'user' | 'ai';
  text: string;
}

@Component({
  selector: 'app-ai-chat',
  imports: [
    RouterLink,
    NgClass,
    FormsModule
  ],
  templateUrl: './ai-chat.component.html',
  styleUrl: './ai-chat.component.css'
})
export class AiChatComponent {
  @ViewChild('chatContainer') private chatContainer!: ElementRef;

  currentMessage = '';
  messages: ChatMessage[] = [
    { from: 'ai', text: 'Ahoj! Ja som tvoj AI asistent.' },
    { from: 'user', text: 'Ahoj, ako sa máš?' },
    { from: 'ai', text: 'Mám sa dobre, ďakujem! Čím ti môžem pomôcť?' },
  ];

  sendMessage() {
    if (!this.currentMessage.trim()) return;

    this.messages.push({ from: 'user', text: this.currentMessage });

    const userMsg = this.currentMessage;
    this.currentMessage = '';

    // fake AI odpoveď
    setTimeout(() => {
      const fakeReplies = [
        'Zaujímavá otázka!',
        'To si musíš overiť.',
        'Samozrejme, viem ti s tým pomôcť.',
        'Môžeš skúsiť toto riešenie.'
      ];
      const reply = fakeReplies[Math.floor(Math.random() * fakeReplies.length)];
      this.messages.push({ from: 'ai', text: reply });
      this.scrollToBottom();
    }, 500);

    this.scrollToBottom();
  }

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
}
