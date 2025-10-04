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
import {SplitRequest} from "../../../core/models/split-request";
import {SplitPartDTO} from "../../../core/models/SplitPartDTO";

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
        numberRequested: [''],
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

  async onFetchAccounts() {
    const response = await this.accountCharts.findAccountsByCompany({
      companyId: this.companyId,
    });
    this.groupedAccounts = response.data;
    return response.data;
  }

  onFetchReceipt() {
    this.receiptService.getReceiptById({ receiptId: this.receiptId })
      .then(response => {
        this.receiptResponse = response.data;

        console.log(this.receiptResponse);

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

      a.download = `receipt_${this.receiptResponse.company?.name}.xml`;

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
    this.selectedItemForAccount = item.id;
    this.selectedItemName = item.name;
    this.drawerOpen = true;

    this.onFetchAccounts().then(() => {
      if (!this.selectedAccountsPerItem[item.id] && item.accountValue) {
        const matchingAccount = this.groupedAccounts
          .flatMap(cls => cls.categories)
          .flatMap(cat => cat.accounts)
          .find(acc => acc.number === item.accountValue);

        if (matchingAccount) {
          this.selectedAccountsPerItem[item.id] = matchingAccount;
        }
      }
    });
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
      this.onFetchReceipt()
    });
  }

  isSplitModalOpen = false;
  selectedItem: any = { amount: 100 }; // príklad položky
  splits: { percentage: number; amount: number; account: string }[] = [];

// Otvorenie modalu s prednastavenými hodnotami (napr. 80/20)
  openSplitModal(item: any) {
    this.selectedItem = item;
    this.splits = [
      { percentage: 80, amount: item.amount * 0.8, account: "518" },
      { percentage: 20, amount: item.amount * 0.2, account: "501" }
    ];
    this.isSplitModalOpen = true;
  }

  closeSplitModal() {
    this.isSplitModalOpen = false;
    this.split = {};
    this.secondSplit = {};

  }

  split: SplitPartDTO = {};
  secondSplit: SplitPartDTO = {};

  errorMessage: string | null = null;


  firstItemChangePercent() {
    const price = this.selectedItem.priceWithVAT;
    const percent = this.split.splitPercentage || 0;

    this.split.priceWithVAT = +(price * percent / 100).toFixed(2);
    this.split.priceWithoutVAT = +(this.split.priceWithVAT / (1 + (this.split.vatRate || 0) / 100)).toFixed(2);
    this.split.name = this.selectedItem.name

    this.secondItemAutoUpdate();
  }

  firstItemChangePrice() {
    const price = this.selectedItem.priceWithVAT;
    let value = this.split.priceWithVAT || 0;

    if (value > price) {
      this.errorMessage = 'Hodnota nesmie presiahnuť cenu s DPH!';
      value = price;
    } else {
      this.errorMessage = null;
    }

    this.split.priceWithVAT = value;
    this.split.splitPercentage = +(value / price * 100).toFixed(2);
    this.split.priceWithoutVAT = +(value / (1 + (this.split.vatRate || 0) / 100)).toFixed(2);
    this.split.name = this.selectedItem.name

    this.secondItemAutoUpdate();
  }

  secondItemChangePercent() {
    const price = this.selectedItem.priceWithVAT;
    const percent = this.secondSplit.splitPercentage || 0;

    this.secondSplit.priceWithVAT = +(price * percent / 100).toFixed(2);
    this.secondSplit.priceWithoutVAT = +(this.secondSplit.priceWithVAT / (1 + (this.secondSplit.vatRate || 0) / 100)).toFixed(2);
    this.secondSplit.name = this.selectedItem.name

    this.firstItemAutoUpdate();
  }

  secondItemChangePrice() {
    const price = this.selectedItem.priceWithVAT;
    let value = this.secondSplit.priceWithVAT || 0;

    if (value > price) {
      this.errorMessage = 'Hodnota nesmie presiahnuť cenu s DPH!';
      value = price;
    } else {
      this.errorMessage = null;
    }

    this.secondSplit.priceWithVAT = value;
    this.secondSplit.splitPercentage = +(value / price * 100).toFixed(2);
    this.secondSplit.priceWithoutVAT = +(value / (1 + (this.secondSplit.vatRate || 0) / 100)).toFixed(2);
    this.secondSplit.name = this.selectedItem.name

    this.firstItemAutoUpdate();
  }

  secondItemAutoUpdate() {
    const price = this.selectedItem.priceWithVAT;
    const percent = this.split.splitPercentage || 0;
    const value = this.split.priceWithVAT || 0;

    this.secondSplit.splitPercentage = +(100 - percent).toFixed(2);
    this.secondSplit.priceWithVAT = +(price - value).toFixed(2);
    this.secondSplit.priceWithoutVAT = +(this.secondSplit.priceWithVAT / (1 + (this.split.vatRate || 0) / 100)).toFixed(2);
    this.secondSplit.name = this.selectedItem.name
  }

  firstItemAutoUpdate() {
    const price = this.selectedItem.priceWithVAT;
    const percent = this.secondSplit.splitPercentage || 0;
    const value = this.secondSplit.priceWithVAT || 0;

    this.split.splitPercentage = +(100 - percent).toFixed(2);
    this.split.priceWithVAT = +(price - value).toFixed(2);
    this.split.priceWithoutVAT = +(this.split.priceWithVAT / (1 + (this.split.vatRate || 0) / 100)).toFixed(2);
    this.split.name = this.selectedItem.name
  }

  onSubmit() {
    const splitRequest: SplitRequest = {
      parts: [this.split, this.secondSplit]
    };

    console.log(splitRequest);

    this.receiptService.splitItem({
        itemId: this.selectedItem.id,
        request: splitRequest
      }
    ).then(r => {
      this.onFetchReceipt()
      this.closeSplitModal()
    })

  }
}
