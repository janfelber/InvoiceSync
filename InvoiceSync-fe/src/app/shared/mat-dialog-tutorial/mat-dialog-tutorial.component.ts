import {Component, ElementRef, EventEmitter, Input, Output, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-mat-dialog-tutorial',
  imports: [
    NgIf,
    NgForOf
  ],
  templateUrl: './mat-dialog-tutorial.component.html',
  styleUrl: './mat-dialog-tutorial.component.css'
})
export class MatDialogTutorialComponent {
  @ViewChildren('videoPlayer') videoPlayers!: QueryList<ElementRef<HTMLVideoElement>>;
  @Input() show = false;
  @Input() title = 'Modal Title';
  @Input() tutorialSelectionName = 'Tutorial Selection';
  @Input() tutorialsByApp: Record<string, { src: string; text: string; thumbnail: string; thumbnailText?: string }[]> = {};
  @Input() availableApps: string[] = [];
  @Output() closeModal = new EventEmitter<void>();

  selectedApp: string = '';
  tutorials: { src: string; text: string }[] = [];
  currentTutorialIndex = 0;

  selectApp(app: string) {
    this.selectedApp = app;
    this.tutorials = this.tutorialsByApp[app] || [];
    this.currentTutorialIndex = 0;
    setTimeout(() => this.playCurrentVideo());
  }

  get currentTutorial() {
    return this.tutorials[this.currentTutorialIndex] ?? { src: '', text: '' };
  }

  playCurrentVideo() {
    this.videoPlayers?.forEach((videoRef, index) => {
      const video = videoRef.nativeElement;

      if (index === this.currentTutorialIndex) {
        video.currentTime = 0;
        video.play().catch(() => {});
      } else {
        video.pause();
      }
    });
  }

  goBackToAppSelection() {
    this.selectedApp = '';
    this.tutorials = [];
    this.currentTutorialIndex = 0;
  }

  prevTutorial() {
    if (this.currentTutorialIndex > 0) {
      this.currentTutorialIndex--;
      setTimeout(() => this.playCurrentVideo());
    }
  }

  nextTutorial() {
    if (this.currentTutorialIndex < this.tutorials.length - 1) {
      this.currentTutorialIndex++;
      setTimeout(() => this.playCurrentVideo());
    }
  }

  onClose() {
    this.closeModal.emit();
    this.currentTutorialIndex = 0;
    this.selectedApp = '';
    this.tutorials = [];
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.onClose();
    }
  }
}
