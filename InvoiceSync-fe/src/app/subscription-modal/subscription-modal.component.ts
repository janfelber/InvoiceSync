import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'subscription-modal',
  imports: [
    NgIf
  ],
  templateUrl: './subscription-modal.component.html',
  styleUrl: './subscription-modal.component.css'
})
export class SubscriptionModalComponent {
  @Input() show = false;
  @Output() closeModal = new EventEmitter<void>();

  onClose() {
    this.closeModal.emit();
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.onClose();
    }
  }

}
