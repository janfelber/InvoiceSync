import { Component } from '@angular/core';
import { InvoiceDisplay } from '../invoice-inspect-display/invoice-inspect-display.component';

@Component({
  selector: 'app-invoice-inspect',
  standalone: true,
  imports: [InvoiceDisplay],
  templateUrl: './invoice-inspect.component.html',
  styleUrl: './invoice-inspect.component.css'
})
export class InvoiceInspect {

}
