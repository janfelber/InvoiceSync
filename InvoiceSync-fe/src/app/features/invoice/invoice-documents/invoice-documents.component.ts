import { Component } from '@angular/core';
import {NgForOf} from "@angular/common";

@Component({
  selector: 'invoice-documents',
  imports: [
    NgForOf
  ],
  templateUrl: './invoice-documents.component.html',
  styleUrl: './invoice-documents.component.css'
})
export class InvoiceDocumentsComponent {

}
