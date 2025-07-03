import {Component, Input, Output, EventEmitter} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'tutorial-modal',
  standalone: true,
  templateUrl: './dialog-tutorial.component.html',
  imports: [NgIf],
})
export class DialogTutorialComponent {
  @Input() show = false;
  @Input() title = 'Modal Title';
  @Input() tutorials: string[] = [];



  @Output() closeModal = new EventEmitter<void>();
  currentTutorialIndex = 0;

  get currentTutorialText() {
    return this.tutorials[this.currentTutorialIndex] ?? '';
  }

  prevTutorial() {
    if (this.currentTutorialIndex > 0) {
      this.currentTutorialIndex--;
    }
  }

  nextTutorial() {
    if (this.currentTutorialIndex < this.tutorials.length - 1) {
      this.currentTutorialIndex++;
    }
  }

  onClose() {
    this.closeModal.emit();
    this.currentTutorialIndex = 0;
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.onClose();
    }
  }
}
