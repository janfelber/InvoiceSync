import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {CommonModule} from "@angular/common";
import {InvoiceDetailResponse} from "../../../pages/invoices/invoice-detail-response";
import {InvoiceService} from "../../../core/services/invoice.service";

@Component({
    selector: 'invoice-info',
    imports: [CommonModule],
    templateUrl: './invoice-info.component.html',
    styleUrl: './invoice-info.component.css'
})
export class InvoiceInspect implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private invoiceService: InvoiceService,
  ) {
  }

  invoiceId: any = null;
  public invoice: InvoiceDetailResponse = {};

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.invoiceId = params.get('id') || '';
    });
    this.onFetchInvoice();
  }

  onFetchInvoice() {
    this.invoiceService.getInvoiceById({ invoiceId: this.invoiceId })
      .then(invoice => {
        this.invoice = invoice.data;
        console.log(this.invoice);
      });
  }
}
