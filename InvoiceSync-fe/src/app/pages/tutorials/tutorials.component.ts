import {Component, ElementRef, ViewChildren, QueryList} from '@angular/core';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';
import {tutorialCategories, TutorialCategory} from '../../shared/tutorials/TutorialCategory';
import {TutorialItem} from '../../shared/tutorials/TutorialItem';

@Component({
  selector: 'app-tutorials',
  imports: [NgClass, NgForOf, NgIf, RouterLink],
  templateUrl: './tutorials.component.html',
})
export class TutorialsComponent {
  categories: TutorialCategory[] = tutorialCategories;
  selectedCategory: TutorialCategory = this.categories[0];
  selectedApp: string = this.firstApp(this.categories[0]);
  playingIndex: number | null = null;

  @ViewChildren('videoRef') videoRefs!: QueryList<ElementRef<HTMLVideoElement>>;

  get apps(): string[] {
    return Object.keys(this.selectedCategory.tutorials);
  }

  get tutorials(): TutorialItem[] {
    return this.selectedCategory.tutorials[this.selectedApp] ?? [];
  }

  get totalCount(): number {
    return this.categories.reduce((sum, c) =>
      sum + Object.values(c.tutorials).reduce((s, arr) => s + arr.length, 0), 0);
  }

  categoryCount(cat: TutorialCategory): number {
    return Object.values(cat.tutorials).reduce((s, arr) => s + arr.length, 0);
  }

  firstApp(cat: TutorialCategory): string {
    return Object.keys(cat.tutorials)[0] ?? '';
  }

  selectCategory(cat: TutorialCategory) {
    this.stopAll();
    this.selectedCategory = cat;
    this.selectedApp = this.firstApp(cat);
    this.playingIndex = null;
  }

  selectApp(app: string) {
    this.stopAll();
    this.selectedApp = app;
    this.playingIndex = null;
  }

  togglePlay(index: number) {
    const videos = this.videoRefs.toArray();
    if (this.playingIndex === index) {
      videos[index]?.nativeElement.pause();
      this.playingIndex = null;
    } else {
      this.stopAll();
      this.playingIndex = index;
      setTimeout(() => {
        const v = this.videoRefs.toArray()[index]?.nativeElement;
        if (v) v.play();
      }, 0);
    }
  }

  stopAll() {
    this.videoRefs?.toArray().forEach(r => {
      r.nativeElement.pause();
      r.nativeElement.currentTime = 0;
    });
    this.playingIndex = null;
  }

  onVideoEnded() {
    this.playingIndex = null;
  }
}
