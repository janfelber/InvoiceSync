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
  ],
  templateUrl: './invoice-inspect.component.html',
  styleUrl: './invoice-inspect.component.css'
})
export class InvoiceDisplay implements OnInit{

  constructor(
    private route: ActivatedRoute,
  ) {

  }

  invoiceId: any = null;

  public invoice: InvoiceDetailResponse = {};

  ngOnInit(): void {
    initFlowbite();
    this.route.paramMap.subscribe(params => {
      this.invoiceId = params.get('id') || '';
    });
  }

}
