import {Component, OnInit} from '@angular/core';
import {AxiosService} from "../../../core/axios.service";
import {ActivatedRoute} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {InvoiceDetailResponse} from "../../../pages/invoices/invoice-detail-response";
import {InvoiceService} from "../../../core/services/invoice.service";

@Component({
    selector: 'invoice-info',
    imports: [FormsModule, CommonModule],
    templateUrl: './invoice-info.component.html',
    styleUrl: './invoice-info.component.css'
})
export class InvoiceInspect implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private invoiceService: InvoiceService
  ) {
  }

  invoiceId: any = null;

  drawerOpen = false;

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
      })
  }

  openDrawer(item: any): void {
    this.drawerOpen = true;
  }

  closeDrawer() {
    this.drawerOpen = false;
  }
}
