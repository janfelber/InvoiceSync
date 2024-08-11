import { Component } from '@angular/core';
import { InvoiceDisplay } from '../invoice-inspect-display/invoice-inspect-display.component';
import { InvoiceInspectOcrWarning } from '../invoice-inspect-ocr-warning/invoice-inspect-ocr-warning.component';

@Component({
  selector: 'app-invoice-inspect',
  standalone: true,
  imports: [InvoiceDisplay, InvoiceInspectOcrWarning],
  templateUrl: './invoice-inspect.component.html',
  styleUrl: './invoice-inspect.component.css'
})
export class InvoiceInspect {

}
