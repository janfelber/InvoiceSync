import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {NgClass, NgForOf } from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {AxiosService} from "../axios.service";
import {ReceiptRequest} from "../models/receipt-request";
import {InvoiceService} from "../page/invoices/invoice.service";
import {ToastrService} from "ngx-toastr";
import { CommonModule } from '@angular/common';
import {initFlowbite} from 'flowbite'
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {ReceiptService} from "../services/receipt.service";
import {ReceiptDetailResponse} from "../receipts/receipt-detail-response";

@Component({
  selector: 'app-receipt-details',
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
    MatDialogWindowComponent
  ],
  templateUrl: './receipt-details.component.html',
  styleUrl: './receipt-details.component.css'
})
export class ReceiptDetailsComponent implements OnInit {
  constructor(
    private receiptService: ReceiptService,
    private route: ActivatedRoute,
    private invoiceService: InvoiceService,
    private toastr: ToastrService,
  ) {
  }

  receiptId: any = null;
  companyId: any = null;
  @ViewChild('successToast') successToast!: ElementRef;

  modalOpen = false;
  receipt: any = {};
  items: ReceiptRequest['items'] = [];
  partner: any = {};
  myIdentity: any = {};
  receiptResponse: ReceiptDetailResponse = {};

  receiptRequest: ReceiptRequest = {};

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.receiptId = params.get('id') || '';
    });
    this.onFetchReceipt();
    initFlowbite();
  }

  onFetchReceipt() {
    this.receiptService.getReceiptById({
      receiptId: this.receiptId
    }).then(response => {
      this.receiptResponse = response.data
      console.log("test", this.receiptResponse);
      // this.companyId = data.company;
      // this.receipt = data.receiptDetails;
      // this.partner = data.partner;
      // this.items = data.items;
    });
  }

  get subtotal(): number {
    return this.items?.reduce((sum, item) => sum + (item.priceWithoutVAT * (item.quantity ?? 1)), 0) ?? 0;
  }

  get totalVAT(): number {
    return this.items?.reduce((sum, item) => {
      const quantity = item.quantity ?? 1;
      return sum + ((item.priceWithVAT - item.priceWithoutVAT) * quantity);
    }, 0) ?? 0;
  }

  get totalPrice(): number {
    return this.items?.reduce((sum, item) => sum + (item.priceWithVAT * (item.quantity ?? 1)), 0) ?? 0;
  }

  getTotalDiscount(): number {
    if (!this.items) return 0;

    return this.items
      .filter(item => item.priceWithVAT < 0)
      .reduce((sum, item) => sum + item.priceWithVAT * (item.quantity ?? 1), 0);
  }
  getDiscountAmount(item: any): number {
    if (!item.priceOriginal || item.priceOriginal <= item.priceWithVAT) {
      return 0;
    }
    return item.priceOriginal - item.priceWithVAT;
  }

  // async exportReceipt() {
  //   // await this.myIdentityCompany();
  //
  //   this.receiptRequest = {
  //     receiptDetails: {
  //       numberRequested: 'test',
  //       date: this.receipt.date,
  //       datePayment: this.receipt.datePayment,
  //       dateTax: this.receipt.dateTax,
  //       accountValue: this.receipt.accountValue,
  //       classificationVAT: this.receipt.classificationVAT,
  //       classificationKVVAT: this.receipt.classificationKVVAT,
  //       description: this.receipt.description
  //     },
  //     partner: {
  //       name: this.partner.name,
  //       city: this.partner.city,
  //       street: this.partner.street,
  //       zip: this.partner.zip,
  //       registrationNumber: this.partner.registrationNumber,
  //       taxId: this.partner.taxId,
  //       vatId: this.partner.vatId
  //     },
  //     myIdentity: {
  //       name: this.receiptResponse.company?.name,
  //       city: this.receiptResponse.company?.city,
  //       street: this.receiptResponse.company?.street,
  //       zip: this.receiptResponse.company?.zip,
  //       taxId: this.receiptResponse.company?.taxId,
  //       vatId: this.receiptResponse.company?.vatId,
  //       registrationNumber: this.receiptResponse.company?.registrationNumber
  //     },
  //     items: this.items
  //     };
  //
  //   console.log(this.receiptRequest)
  //
  //   this.invoiceService.exportPohodaReceipt(this.receiptRequest).subscribe({
  //     next: (response) => {
  //       const blob = new Blob([response], {type: 'application/xml'});
  //       const url = window.URL.createObjectURL(blob);
  //       const a = document.createElement('a');
  //       a.href = url;
  //       a.download = 'receipt.xml';
  //       document.body.appendChild(a);
  //       a.click();
  //       document.body.removeChild(a);
  //       window.URL.revokeObjectURL(url);
  //
  //       console.log(response)
  //       this.toastr.success('Export uspesny', 'Informácia',
  //         {
  //           timeOut: 1500,
  //           progressBar: true,
  //           progressAnimation: 'increasing',
  //           closeButton: true,
  //           positionClass: 'toast-top-right',
  //         });
  //     },
  //     error: (error) => {
  //       console.error('Chyba pri exportovaní faktúry', error);
  //     }
  //   });
  // }

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  async updateReceipt() {

    this.receiptRequest = {
        datePayment:this.receiptResponse?.receiptDetails?.datePayment,
        date: this.receiptResponse.receiptDetails?.date,
        dateTax: this.receiptResponse.receiptDetails?.dateTax,
        accounting: this.receiptResponse.receiptDetails?.accountValue,
        classificationVAT: this.receiptResponse.receiptDetails?.classificationVAT,
        classificationKVVAT: this.receiptResponse.receiptDetails?.classificationKVVAT,
        description: this.receiptResponse.receiptDetails?.description,
        partnerName: this.receiptResponse.partner?.name,
        partnerCity: this.receiptResponse.partner?.city,
        partnerStreet: this.receiptResponse.partner?.street,
        partnerZip: this.receiptResponse.partner?.zip,
        partnerRegistrationNumber: this.receiptResponse.partner?.registrationNumber,
        partnerTaxId: this.receiptResponse.partner?.taxId,
        partnerVatId: this.receiptResponse.partner?.vatId,
      items: this.items
      }

    this.receiptService.updateReceipt({
      receiptId: this.receiptId,
      receipt: this.receiptRequest
    }).then(() =>{
      this.modalOpen = false;
      this.toastr.success(
        'Zmeny bločka uložené',
        '',
        {
          timeOut: 3000,
          progressBar: true,
          progressAnimation: 'increasing',
          closeButton: true,
          positionClass: 'toast-top-right',
        }
      );
    });
  }
}
