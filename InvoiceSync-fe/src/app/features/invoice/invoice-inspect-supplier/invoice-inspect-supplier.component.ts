import { Component, inject } from '@angular/core';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-invoice-inspect-supplier',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './invoice-inspect-supplier.component.html',
  styleUrl: './invoice-inspect-supplier.component.css'
})
export class InvoiceInspectSupplier {
  private matIconRegistry = inject(MatIconRegistry)
  private domSanitizer = inject(DomSanitizer)

  constructor() {
    this.matIconRegistry.addSvgIcon('custom_icon', this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/question.svg'))
  }
}
