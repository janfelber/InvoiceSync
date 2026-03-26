import {Component, ElementRef, EventEmitter, HostListener, Input, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgClass, NgForOf, NgIf} from '@angular/common';

export interface SelectOption {
  label: string;
  value: any;
}

@Component({
  selector: 'app-searchable-select',
  imports: [FormsModule, NgForOf, NgIf, NgClass],
  templateUrl: './searchable-select.component.html',
})
export class SearchableSelectComponent {

  @Input() options: SelectOption[] = [];
  @Input() value: any = null;
  @Input() placeholder = 'Vybrať...';
  @Input() disabled = false;
  @Output() valueChange = new EventEmitter<any>();

  isOpen = false;
  searchQuery = '';

  constructor(private elementRef: ElementRef) {}

  get selectedLabel(): string {
    const found = this.options.find(o => o.value === this.value);
    return found ? found.label : '';
  }

  get filteredOptions(): SelectOption[] {
    const q = this.searchQuery.toLowerCase();
    return q ? this.options.filter(o => o.label.toLowerCase().includes(q)) : this.options;
  }

  toggle() {
    if (this.disabled) return;
    this.isOpen = !this.isOpen;
    if (!this.isOpen) this.searchQuery = '';
  }

  select(option: SelectOption) {
    this.value = option.value;
    this.valueChange.emit(option.value);
    this.isOpen = false;
    this.searchQuery = '';
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isOpen = false;
      this.searchQuery = '';
    }
  }
}
