import { Component } from '@angular/core';
import { GridInvoicesComponent } from "../components/grid-invoices/grid-invoices.component";

@Component({
  selector: 'app-test',
  standalone: true,
  imports: [GridInvoicesComponent],
  templateUrl: './invoices.component.html',
  styleUrl: './invoices.component.css'
})
export class Invoices {

}
