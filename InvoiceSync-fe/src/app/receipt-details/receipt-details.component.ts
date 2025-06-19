import {Component, ElementRef, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {ReceiptRequest} from "../models/receipt-request";
import {ToastrService} from "ngx-toastr";
import {CommonModule} from '@angular/common';
import {initFlowbite} from 'flowbite'
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {ReceiptService} from "../services/receipt.service";
import {ReceiptDetailResponse} from "../receipts/receipt-detail-response";
import {AccountChartService} from "../services/account-chart.service";

interface Account {
  id:number,
  number:string,
  name:string
}

@Component({
  selector: 'app-receipt-details',
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
    MatDialogWindowComponent,
    RouterLink
  ],
  templateUrl: './receipt-details.component.html',
  styleUrl: './receipt-details.component.css'
})
export class ReceiptDetailsComponent implements OnInit {
  constructor(
    private receiptService: ReceiptService,
    private accountCharts: AccountChartService,
    private route: ActivatedRoute,
    private toastr: ToastrService,
  ) {
  }

  receiptId: any = null;
  companyId: any = null;
  @ViewChild('successToast') successToast!: ElementRef;

  modalOpen = false;
  drawerOpen = false;
  receipt: any = {};
  selectedItemName: string = '';

  items: {
    accountText?: string;
    name?: string;
    quantity?: number;
    priceWithoutVAT?: number;
    vatRate?: number;
    priceWithVAT?: number;
    accountValue?: string;
    id?: number;
  }[] = [];


  partner: any = {};
  receiptResponse: ReceiptDetailResponse = {
    partner: {
      city: '',
      name: '',
      registrationNumber: '',
      street: '',
      taxId: '',
      vatId: '',
      zip: ''
    }
  };

  receiptRequest: ReceiptRequest = {
    items: []
  };

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.receiptId = params.get('id') || '';
    });
    initFlowbite();
    this.onFetchReceipt();
    this.onFetchAccounts();
  }

  groupedAccounts: any[] = [];

  onFetchAccounts() {
    this.accountCharts.finalAll().then(response => {
      console.log(response.data)
      this.groupedAccounts = response.data;
    })
  }

  onFetchReceipt() {
    this.receiptService.getReceiptById({
      receiptId: this.receiptId
    }).then(response => {
      this.receiptResponse = response.data
      console.log("toto pride", this.receiptResponse)
      this.items = response.data.items;
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
      items: this.items
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
        if (error.response?.status === 429) {
          const message = 'Mesačný limit pre export bločkov vyčerpaný.';
          this.toastr.warning(
            message,
            '',
            {
              timeOut: 5000,
              progressBar: true,
              progressAnimation: 'increasing',
              closeButton: true,
              positionClass: 'toast-top-right',
            });
        } else {
          console.log(error.response.data)
          this.toastr.error(
            'Chyba pri exporte',
            '',
            {
              timeOut: 3000,
              progressBar: true,
              progressAnimation: 'increasing',
              closeButton: true,
              positionClass: 'toast-top-right',
            });
          console.error('Error exporting Excel:', error);
        }
      });
  }

  selectedAccountsPerItem: { [itemId: number]: Account } = {};

  assignAccountToItem(): void {
    const selectedAccount = this.selectedAccountsPerItem[this.selectedItemForAccount];
    if (!selectedAccount) {
      console.warn('Žiadny účet nie je vybraný pre item:', this.selectedItemForAccount);
      return;
    }

    this.items.forEach(item => {
      if (item.id === this.selectedItemForAccount) {
        item.accountValue = selectedAccount.number;
        console.log('Priradený účet', item.accountValue, 'pre item', item.id);
      }
    });

    this.closeDrawer();
    this.selectedItemForAccount = null;
  }

  onAccountChange(itemId: number, account: Account) {
    this.selectedAccountsPerItem[itemId] = account;
    console.log('Zvolený účet:', account.id, 'pre item:', itemId);
  }

  selectedItemForAccount: any = null;

  openDrawer(item: any): void {
    this.selectedItemForAccount = item.id;

    this.selectedItemName = item.name;
    console.log(item)

    if (!this.selectedAccountsPerItem[item.id] && item.accountValue) {
      const matchingAccount = this.groupedAccounts
          .flatMap(cls => cls.categories)
          .flatMap(cat => cat.accounts)
          .find(acc => acc.number === item.accountValue);

      if (matchingAccount) {
        this.selectedAccountsPerItem[item.id] = matchingAccount;
      }
    }

    this.drawerOpen = true;
  }

  closeDrawer() {
    this.drawerOpen = false;
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
