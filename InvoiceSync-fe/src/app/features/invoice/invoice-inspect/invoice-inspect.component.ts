import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from "@angular/router";
import {initFlowbite} from "flowbite";
import {InvoiceService} from "../../../core/services/invoice.service";
import {InvoiceDetailResponse} from "../../../pages/invoices/invoice-detail-response";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InvoiceInspect} from "../invoice-info/invoice-info.component";
import {
  DataTransferExcelInspectComponent
} from "../../../pages/data-transfer-excel-inspect/data-transfer-excel-inspect.component";
import {DataTransferMappingComponent} from "../../../pages/data-transfer-mapping/data-transfer-mapping.component";
import {InvoiceDocumentsComponent} from "../invoice-documents/invoice-documents.component";
import {TranslatePipe} from "@ngx-translate/core";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'invoice-inspect',
  standalone: true,
  imports: [
    RouterLink,
    ReactiveFormsModule,
    FormsModule,
    InvoiceInspect,
    DataTransferExcelInspectComponent,
    DataTransferMappingComponent,
    InvoiceDocumentsComponent,
    TranslatePipe,
    CommonModule,
  ],
  templateUrl: './invoice-inspect.component.html',
  styleUrl: './invoice-inspect.component.css'
})
export class InvoiceDisplay implements OnInit{

  constructor(
    private route: ActivatedRoute,
    private invoiceService: InvoiceService,
  ) {}

  invoiceId!: number
  public invoice: InvoiceDetailResponse | null = null;

  ngOnInit(): void {
    initFlowbite();
    const invoiceId = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(invoiceId)) {
      console.error('Invalid invoice ID');
      return;
    }

    this.invoiceId = invoiceId;
    this.invoiceService.getInvoiceById(this.invoiceId)
      .subscribe({
        next: invoice => {
          this.invoice = invoice;
        },
        error: error => {
          console.error('Invoice loading failed:', error);
        }
      });
  }

  get statusLabel(): string {
    switch (this.invoice?.invoiceDetails?.status?.toUpperCase()) {
      case 'PAID': return 'Uhradená';
      case 'UNPAID': return 'Neuhradená';
      case 'OVERDUE': return 'Po splatnosti';
      case 'CANCELLED': return 'Stornovaná';
      default: return this.invoice?.invoiceDetails?.status ?? '';
    }
  }

  get statusClasses(): string {
    switch (this.invoice?.invoiceDetails?.status?.toUpperCase()) {
      case 'PAID': return 'bg-green-50 text-green-700 ring-green-600/20 dark:bg-green-900/20 dark:text-green-400';
      case 'UNPAID': return 'bg-yellow-50 text-yellow-700 ring-yellow-600/20 dark:bg-yellow-900/20 dark:text-yellow-400';
      case 'OVERDUE': return 'bg-red-50 text-red-700 ring-red-600/20 dark:bg-red-900/20 dark:text-red-400';
      default: return 'bg-slate-100 text-slate-600 ring-slate-500/20 dark:bg-slate-800 dark:text-slate-400';
    }
  }

}
