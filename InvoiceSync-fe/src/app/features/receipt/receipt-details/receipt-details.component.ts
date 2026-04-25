import {Component, computed, OnInit, signal} from '@angular/core';
import {NgForOf} from "@angular/common";
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ReceiptRequest} from "../../../core/models/receipt-request";
import {CommonModule} from '@angular/common';
import {initFlowbite} from 'flowbite'
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {ReceiptService} from "../../../core/services/receipt.service";
import {ReceiptDetailResponse} from "../../../pages/receipts/receipt-detail-response";
import {PostingAccountService} from "../../../core/services/posting-account.service";
import {ReceiptType} from "../../../core/enums/receipt-type";
import {toast} from "ngx-sonner";
import {TranslateModule, TranslateService} from "@ngx-translate/core";

interface Account {
  id: number,
  number: string,
  name: string
}

@Component({
  selector: 'app-receipt-details',
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
    MatDialogWindowComponent,
    TranslateModule,
  ],
  templateUrl: './receipt-details.component.html',
  styleUrl: './receipt-details.component.css'
})
export class ReceiptDetailsComponent implements OnInit {
  constructor(
    private receiptService: ReceiptService,
    private postingAccountService: PostingAccountService,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private translate: TranslateService,
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

  modalOpen = false;
  mergeConfirmOpen = false;
  drawerOpen = false;

  receiptId: any = null;
  companyId: any = null;
  selectedItemForAccount: any = null;
  receipt: any = {};
  selectedAccountsPerItem: { [itemId: number]: Account } = {};
  groupedAccounts = signal<any[]>([]);
  receiptForm!: FormGroup;

  selectedItemName: string = '';
  mergedItemName = '';

  searchQuery = signal('');
  openGroups: { [key: string]: boolean } = {};

  filteredGroupedAccounts = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.groupedAccounts();
    return this.groupedAccounts()
      .map(group => ({
        ...group,
        categories: group.categories
          .map((cat: any) => ({
            ...cat,
            accounts: cat.accounts.filter((acc: any) =>
              acc.number?.toLowerCase().includes(q) ||
              acc.name?.toLowerCase().includes(q)
            )
          }))
          .filter((cat: any) => cat.accounts.length > 0)
      }))
      .filter(group => group.categories.length > 0);
  });

  toggleGroup(key: string): void {
    this.openGroups[key] = !this.openGroups[key];
  }

  currentMode: 'edit' | 'accounting' | 'merge' = 'edit';
  editingSupplier = false;
  editingCell: string | null = null;

  startEditingCell(event: Event, cellKey: string): void {
    if (this.currentMode !== 'edit') return;
    event.stopPropagation();
    this.editingCell = cellKey;
  }
  selectedForMerge = new Set<number>();

  setMode(mode: 'edit' | 'accounting' | 'merge'): void {
    if (mode !== 'merge') {
      this.selectedForMerge.clear();
    }
    this.currentMode = mode;
  }

  toggleMergeSelection(itemId: number | undefined): void {
    if (itemId == null) return;
    if (this.selectedForMerge.has(itemId)) {
      this.selectedForMerge.delete(itemId);
    } else {
      this.selectedForMerge.add(itemId);
    }
  }

  isSelectedForMerge(itemId: number | undefined): boolean {
    if (itemId == null) return false;
    return this.selectedForMerge.has(itemId);
  }

  get vatAmount(): number {
    const withVat = Number(this.receiptResponse?.receiptDetails?.totalPriceWithVat ?? 0);
    const withoutVat = Number(this.receiptResponse?.receiptDetails?.totalPriceWithoutVat ?? 0);
    return withVat - withoutVat;
  }

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
    paymentType: 'CASH',
    items: this.items,
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
      unitPriceWithoutVat: [itemData.unitPriceWithoutVat],
      unitPriceWithVat: [itemData.unitPriceWithVat],
      totalItemPriceWithoutVat: [itemData.totalItemPriceWithoutVat],
      totalItemPriceWithVat: [itemData.totalItemPriceWithVat],
      vatRate: [itemData.vatRate],
      priceWithVAT: [itemData.priceWithVAT],
      accountValue: [itemData.accountValue],
    });
  }

  async onFetchAccounts() {
    const paidByCard = !!this.receiptResponse.receiptDetails?.paymentType;
    const type = paidByCard ? ReceiptType.INTERNAL : ReceiptType.CASH;

    const response = await this.postingAccountService.findAvailableAccounts({
      companyId: this.companyId,
      type: type,
    });
    this.groupedAccounts.set(response.data);
    return response.data;
  }

  onFetchReceipt() {
    this.receiptService.getReceiptById({receiptId: this.receiptId})
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
    try {
      const response = await this.receiptService.exportReceiptPohoda(this.receiptId);

      this.modalOpen = false;
      toast.success(this.translate.instant('RECEIPT_DETAIL.TOAST_EXPORT_SUCCESS'));

      const blob = new Blob([response.data], {type: 'application/octet-stream'});

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
        toast.warning(this.translate.instant('RECEIPT_DETAIL.TOAST_EXPORT_LIMIT'));
      } else if (error.response?.status === 422) {
        toast.warning(this.translate.instant('RECEIPT_DETAIL.TOAST_NUMBERING_NOT_SET'));
      } else {
        toast.error(this.translate.instant('RECEIPT_DETAIL.TOAST_EXPORT_ERROR'));
      }
    }
  }

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
    this.searchQuery.set('');
    this.selectedItemForAccount = null;
  }

  onAccountChange(itemId: number, account: Account) {
    this.selectedAccountsPerItem[itemId] = account;
    console.log('Zvolený účet:', account.id, 'pre item:', itemId);
  }

  openDrawer(item: any): void {
    this.selectedItemForAccount = item.id;
    this.selectedItemName = item.name;
    this.drawerOpen = true;

    this.onFetchAccounts().then(() => {
      if (!this.selectedAccountsPerItem[item.id] && item.accountValue) {
        const matchingAccount = this.groupedAccounts()
          .flatMap((cls: any) => cls.categories)
          .flatMap((cat: any) => cat.accounts)
          .find((acc: any) => acc.number === item.accountValue);

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
      paymentType: this.receiptResponse.receiptDetails?.paymentType ?? 'CASH',
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
      items: this.items.map(item => ({
        ...item,
        description: item.name
      }))
    }

    this.receiptService.updateReceipt({
      receiptId: this.receiptId,
      receipt: this.receiptRequest
    }).then(() => {
      this.modalOpen = false;
      toast.success(this.translate.instant('RECEIPT_DETAIL.TOAST_SAVE_SUCCESS'));
      this.onFetchReceipt()
    });
  }

  get selectedItemsForMerge(): any[] {
    return (this.receiptResponse.items || []).filter((item: any) => this.selectedForMerge.has(item.id));
  }

  get mergeTotal(): number {
    return this.selectedItemsForMerge.reduce((sum, item) => sum + (item.totalItemPriceWithVat || 0), 0);
  }

  get hasMixedVatRates(): boolean {
    const rates = new Set(this.selectedItemsForMerge.map(item => item.vatRate));
    return rates.size > 1;
  }

  get dominantVatRate(): number {
    const counts = new Map<number, number>();
    for (const item of this.selectedItemsForMerge) {
      counts.set(item.vatRate, (counts.get(item.vatRate) ?? 0) + 1);
    }
    let dominant = this.selectedItemsForMerge[0]?.vatRate;
    let max = 0;
    for (const [rate, count] of counts) {
      if (count > max) { max = count; dominant = rate; }
    }
    return dominant;
  }

  mergeItems(): void {
    this.mergedItemName = this.selectedItemsForMerge[0]?.name ?? '';
    this.mergeConfirmOpen = true;
  }

  async confirmMerge(): Promise<void> {
    this.receiptService.mergeReceiptItems(
      this.receiptId,
      { itemIds: Array.from(this.selectedForMerge), description: this.mergedItemName }
    ).then(() => {
      this.mergeConfirmOpen = false;
      this.selectedForMerge.clear();
      this.onFetchReceipt();
      toast.success(this.translate.instant('RECEIPT_DETAIL.TOAST_MERGE_SUCCESS'));
    });
  }

  togglePaymentType() {
    const details = this.receiptResponse.receiptDetails;
    if (!details) return;
    details.paymentType = details.paymentType === 'CARD' ? 'CASH' : 'CARD';
  }
}
