import {Component, ElementRef, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgForOf} from "@angular/common";
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {ReceiptRequest} from "../../../core/models/receipt-request";
import {ToastrService} from "ngx-toastr";
import {CommonModule} from '@angular/common';
import {initFlowbite} from 'flowbite'
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {ReceiptService} from "../../../core/services/receipt.service";
import {ReceiptDetailResponse} from "../../../pages/receipts/receipt-detail-response";
import {AccountChartService} from "../../../core/services/account-chart.service";

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
    private fb: FormBuilder,
    private toastr: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.receiptId = params.get('id') || '';
    });
    initFlowbite();
    this.initForm();
    this.onFetchReceipt();
  }

  receiptId: any = null;
  companyId: any = null;
  @ViewChild('successToast') successToast!: ElementRef;

  modalOpen = false;
  drawerOpen = false;
  receipt: any = {};
  selectedItemName: string = '';

  receiptForm!: FormGroup;
  groupedAccounts: any[] = [];

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



  private initForm(): void {
    this.receiptForm = this.fb.group({
      receiptDetails: this.fb.group({
        numberRequested: ['neviem ci toto pojde'],
        date: ['', Validators.required],
        paidByCard: [false],
        datePayment: [''],
        dateTax: [''],
        accountValue: [''],
        classificationVAT: [''],
        classificationKVVAT: [''],
        description: ['']
      }),
      partner: this.fb.group({
        name: ['', Validators.required],
        city: [''],
        street: [''],
        zip: [''],
        registrationNumber: [''],
        taxId: [''],
        vatId: ['']
      }),
      myIdentity: this.fb.group({
        id: [null],
        name: ['', Validators.required],
        surname: [''],
        city: [''],
        street: [''],
        streetNumber: [''],
        zip: [''],
        registrationNumber: [''],
        taxId: [''],
        vatId: ['']
      }),
      items: this.fb.array([])
    });
  }

  createItem(itemData: any): FormGroup {
    return this.fb.group({
      id: [itemData.id],
      accountText: [itemData.accountText],
      name: [itemData.name],
      quantity: [itemData.quantity],
      priceWithoutVAT: [itemData.priceWithoutVAT],
      vatRate: [itemData.vatRate],
      priceWithVAT: [itemData.priceWithVAT],
      accountValue: [itemData.accountValue],
    });
  }

  onFetchAccounts() {
    this.accountCharts.findAccountsByCompany({
      companyId: this.companyId,
    }).then(response => {
      this.groupedAccounts = response.data;
    })
  }

  onFetchReceipt() {
    this.receiptService.getReceiptById({ receiptId: this.receiptId })
      .then(response => {
        this.receiptResponse = response.data;

        this.items = this.receiptResponse.items || [];
        this.companyId = this.receiptResponse.company?.id ?? null;

        if (this.receiptResponse.company) {
          this.receiptForm.get('myIdentity')?.patchValue(this.receiptResponse.company);
        }

        if (this.receiptResponse.receiptDetails) {
          this.receiptForm.get('receiptDetails')?.patchValue(this.receiptResponse.receiptDetails);
        }

        if (this.receiptResponse.partner) {
          this.receiptForm.get('partner')?.patchValue(this.receiptResponse.partner);
        }

        const itemsFA = this.receiptForm.get('items') as FormArray;
        itemsFA.clear();  // vymaž existujúce ak sú
        this.receiptResponse.items?.forEach((item: any) => {
          itemsFA.push(this.createItem(item));
        });

        // Tu vypíš formu:
        console.log('Celá forma:', this.receiptForm.value);
        console.log('myIdentity:', this.receiptForm.get('myIdentity')?.value);
        console.log('receiptDetails:', this.receiptForm.get('receiptDetails')?.value);
        console.log('partner:', this.receiptForm.get('partner')?.value);
      })
      .catch(error => {
        console.error('Chyba pri načítaní bločku:', error);
      });
  }

  async exportReceiptXml() {
    if (this.receiptForm.invalid) {
      this.toastr.error('Formulár obsahuje chyby, oprav ich prosím.');
      return;
    }

    const formValue = this.receiptForm.value;

    const receiptRequest = {
      receiptDetails: formValue.receiptDetails,
      partner: formValue.partner,
      myIdentity: formValue.myIdentity,
      items: formValue.items,
    };

    console.log("Posielam na export:", receiptRequest);

    try {
      const response = await this.receiptService.exportReceiptPohoda({ receipt: receiptRequest });

      this.modalOpen = false;
      this.toastr.success('Bloček exportovaný úspešne', '', {
        timeOut: 3000,
        progressBar: true,
        progressAnimation: 'increasing',
        closeButton: true,
        positionClass: 'toast-top-right',
      });

      const blob = new Blob([response.data], { type: 'application/xml' });

      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;

      a.download = `receipt_${new Date().toISOString()}.xml`;

      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);

      window.URL.revokeObjectURL(url);

    } catch (error: any) {
      if (error.response?.status === 429) {
        this.toastr.warning('Mesačný limit pre export bločkov vyčerpaný.', '', {
          timeOut: 5000,
          progressBar: true,
          progressAnimation: 'increasing',
          closeButton: true,
          positionClass: 'toast-top-right',
        });
      } else {
        console.error('Chyba pri exporte:', error);
        this.toastr.error('Chyba pri exporte', '', {
          timeOut: 3000,
          progressBar: true,
          progressAnimation: 'increasing',
          closeButton: true,
          positionClass: 'toast-top-right',
        });
      }
    }
  }

  async exportReceipt() {
    // await this.myIdentityCompany();

    this.receiptRequest = {
      datePayment: this.receiptResponse?.receiptDetails?.datePayment,
      receiptNumber: "testCard",
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
    this.onFetchAccounts();
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
