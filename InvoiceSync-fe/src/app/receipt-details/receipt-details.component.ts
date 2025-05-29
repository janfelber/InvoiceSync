import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {NgClass, NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {AxiosService} from "../axios.service";
import {ReceiptRequest} from "../models/receipt-request";
import {InvoiceService} from "../page/invoices/invoice.service";
import {ToastrService} from "ngx-toastr";
import {CommonModule} from '@angular/common';
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

  receiptRequest: ReceiptRequest = {
    items: []
  };

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
      console.log("toto pride", this.receiptResponse)
      // this.companyId = data.company;
      // this.receipt = data.receiptDetails;
      // this.partner = data.partner;
      // this.items = data.items;
    });
  }

  async exportReceipt() {
    // await this.myIdentityCompany();

    this.receiptRequest = {
      datePayment: this.receiptResponse?.receiptDetails?.datePayment,
      receiptNumber: "1234",
      date: this.receiptResponse.receiptDetails?.date,
      dateTax: this.receiptResponse.receiptDetails?.dateTax,
      accounting: this.receiptResponse.receiptDetails?.accountValue,
      isPaidByCard: this.receiptResponse.receiptDetails?.paidByCard,
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
      totalPrice: this.receiptResponse.receiptDetails?.totalPrice,
      items: this.receiptResponse.items?.map(item => ({
        id: item.id,
        name: item.name,
        quantity: item.quantity ?? 1,
        priceWithoutVat: item.priceWithoutVAT ?? 0,
        vatRate: item.vatRate ?? 20,
        accountValue: item.accountValue ?? undefined,
        priceWithVat: item.priceWithVAT ?? 0,
        accountText: item.accountText ?? undefined
      })) ?? []
    }

    console.log("post", this.receiptRequest)

    this.receiptService.exportReceiptPohoda(
      {
        receipt: this.receiptRequest
      }
    ).then((response) => {
      this.modalOpen = false;
      this.toastr.success(
        'Blocek out',
        '',
        {
          timeOut: 3000,
          progressBar: true,
          progressAnimation: 'increasing',
          closeButton: true,
          positionClass: 'toast-top-right',
        });

      const blob = new Blob([response.data], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      });

      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = this.receiptRequest.receiptNumber + '.xlsx';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    })
      .catch(error => {
        console.error('Error exporting Excel:', error);
      });
  }

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  async updateReceipt() {

    this.receiptRequest = {
      datePayment: this.receiptResponse?.receiptDetails?.datePayment,
      receiptNumber: this.receiptResponse?.receiptNumber,
      date: this.receiptResponse.receiptDetails?.date,
      dateTax: this.receiptResponse.receiptDetails?.dateTax,
      accounting: this.receiptResponse.receiptDetails?.accountValue,
      isPaidByCard: this.receiptResponse.receiptDetails?.paidByCard,
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
    }).then(() => {
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
