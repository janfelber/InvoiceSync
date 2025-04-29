import { Component, Input, Output, EventEmitter } from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-modal',
  standalone: true,
  templateUrl: './mat-dialog-window.component.html',
  imports: [
    NgIf
  ],
})
export class MatDialogWindowComponent {
  @Input() show = false;
  @Input() title = 'Modal Title';
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
