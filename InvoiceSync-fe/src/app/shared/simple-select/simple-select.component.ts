import { Component, ElementRef, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { NgClass, NgForOf, NgIf } from '@angular/common';

export interface SelectOption {
  label: string;
  value: any;
}

@Component({
  selector: 'app-simple-select',
  imports: [NgForOf, NgIf, NgClass],
  templateUrl: './simple-select.component.html',
})
export class SimpleSelectComponent {

  @Input() options: SelectOption[] = [];
  @Input() value: any = null;
  @Input() placeholder = 'Vybrať...';
  @Input() disabled = false;
  @Output() valueChange = new EventEmitter<any>();

  isOpen = false;

  constructor(private elementRef: ElementRef) {}

  get selectedLabel(): string {
    const found = this.options.find(o => o.value === this.value);
    return found ? found.label : '';
  }

  toggle() {
    if (this.disabled) return;
    this.isOpen = !this.isOpen;
  }

  select(option: SelectOption) {
    this.value = option.value;
    this.valueChange.emit(option.value);
    this.isOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isOpen = false;
    }
  }
}
