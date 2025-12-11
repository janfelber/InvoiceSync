import { Component } from '@angular/core';
import {RouterLink} from "@angular/router";
import {InvoiceDocumentsComponent} from "../../invoice/invoice-documents/invoice-documents.component";
import {InvoiceInspect} from "../../invoice/invoice-info/invoice-info.component";
import {ReceiptDetailsComponent} from "../receipt-details/receipt-details.component";
import {ReceiptDocumentsComponent} from "../receipt-documents/receipt-documents.component";
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-receipt-inspect',
  imports: [
    RouterLink,
    InvoiceDocumentsComponent,
    InvoiceInspect,
    ReceiptDetailsComponent,
    ReceiptDocumentsComponent,
    TranslatePipe
  ],
  templateUrl: './receipt-inspect.component.html',
  styleUrl: './receipt-inspect.component.css'
})
export class ReceiptInspectComponent {

}
