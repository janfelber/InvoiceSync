import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {DatePipe, DecimalPipe, NgClass} from "@angular/common";
import {toSignal} from "@angular/core/rxjs-interop";
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ReceiptRequest} from "../../../core/models/receipt-request";
import {initFlowbite} from 'flowbite';
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {ReceiptService} from "../../../core/services/receipt.service";
import {ReceiptDetailResponse} from "../../../pages/receipts/receipt-detail-response";
import {PostingAccountService} from "../../../core/services/posting-account.service";
import {ReceiptType} from "../../../core/enums/receipt-type";
import {toast} from "ngx-sonner";
import {TranslateModule, TranslateService} from "@ngx-translate/core";

interface Account {
  id: number;
  number: string;
  name: string;
}

@Component({
  selector: 'app-receipt-details',
  imports: [
    ReactiveFormsModule,
    FormsModule,
    NgClass,
    DatePipe,
    DecimalPipe,
    MatDialogWindowComponent,
    TranslateModule,
  ],
  templateUrl: './receipt-details.component.html',
  styleUrl: './receipt-details.component.css'
})
export class ReceiptDetailsComponent implements OnInit {

  // ── Services ────────────────────────────────────────────────────────────────
  private readonly receiptService = inject(ReceiptService);
  private readonly postingAccountService = inject(PostingAccountService);
  private readonly fb = inject(FormBuilder);
  private readonly translate = inject(TranslateService);

  // ── Route ────────────────────────────────────────────────────────────────────
  private readonly paramMap = toSignal(inject(ActivatedRoute).paramMap);
  readonly receiptId = computed(() => Number(this.paramMap()?.get('id') ?? 0));

  // ── State signals ────────────────────────────────────────────────────────────
  groupedAccounts = signal<any[]>([]);
  searchQuery = signal('');
  modalOpen = signal(false);
  mergeConfirmOpen = signal(false);
  drawerOpen = signal(false);

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

  // ── Plain properties ─────────────────────────────────────────────────────────

  companyId: number | null = null;
  selectedItemForAccount = signal<number | null>(null);
  selectedAccountsPerItem = signal<{ [itemId: number]: Account }>({});
  receiptForm!: FormGroup;

  selectedItemName = '';
  mergedItemName = '';

  openGroups: { [key: string]: boolean } = {};

  currentMode: 'edit' | 'accounting' | 'merge' = 'edit';
  editingSupplier = false;
  editingCell: string | null = null;
  selectedForMerge = new Set<number>();

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
    items: this.items,
  };

  // ── Getters ──────────────────────────────────────────────────────────────────
  get vatAmount(): number {
    const withVat = Number(this.receiptResponse?.receiptDetails?.totalPriceWithVat ?? 0);
    const withoutVat = Number(this.receiptResponse?.receiptDetails?.totalPriceWithoutVat ?? 0);
    return withVat - withoutVat;
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

  // ── Lifecycle ────────────────────────────────────────────────────────────────
  ngOnInit(): void {
    initFlowbite();
    this.initForm();
    this.onFetchReceipt();
  }

  // ── Private methods ──────────────────────────────────────────────────────────
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

  private createItem(itemData: any): FormGroup {
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

  // ── Public methods ───────────────────────────────────────────────────────────
  async onFetchReceipt(): Promise<void> {
    try {
      const response = await this.receiptService.getReceiptById({receiptId: this.receiptId()});
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
      itemsFA.clear();
      this.receiptResponse.items?.forEach((item: any) => {
        itemsFA.push(this.createItem(item));
      });
    } catch (error) {
      console.error('Chyba pri načítaní bločku:', error);
    }
  }

  async onFetchAccounts(): Promise<any> {
    const paidByCard = !!this.receiptResponse.receiptDetails?.paidByCard;
    const type = paidByCard ? ReceiptType.INTERNAL : ReceiptType.CASH;

    const response = await this.postingAccountService.findAvailableAccounts({
      companyId: this.companyId!,
      type: type,
    });
    this.groupedAccounts.set(response.data);
    return response.data;
  }

  async exportReceiptXml(): Promise<void> {
    if (this.receiptForm.invalid) {
      toast.error(this.translate.instant('RECEIPT_DETAIL.TOAST_FORM_ERRORS'));
      return;
    }

    const formValue = this.receiptForm.value;
    const receiptRequest = {
      receiptDetails: formValue.receiptDetails,
      partner: formValue.partner,
      myIdentity: formValue.myIdentity,
      items: formValue.items,
    };

    try {
      const response = await this.receiptService.exportReceiptPohoda({receipt: receiptRequest});

      this.modalOpen.set(false);
      toast.success(this.translate.instant('RECEIPT_DETAIL.TOAST_EXPORT_SUCCESS'));

      const blob = new Blob([response.data], {type: 'application/xml'});
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

  async updateReceipt(): Promise<void> {
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
      items: this.items.map(item => ({
        ...item,
        description: item.name
      }))
    };

    await this.receiptService.updateReceipt({
      receiptId: this.receiptId(),
      receipt: this.receiptRequest
    });
    this.modalOpen.set(false);
    toast.success(this.translate.instant('RECEIPT_DETAIL.TOAST_SAVE_SUCCESS'));
    this.onFetchReceipt();
  }

  async confirmMerge(): Promise<void> {
    await this.receiptService.mergeReceiptItems(
      this.receiptId(),
      {itemIds: Array.from(this.selectedForMerge), description: this.mergedItemName}
    );
    this.mergeConfirmOpen.set(false);
    this.selectedForMerge.clear();
    this.onFetchReceipt();
    toast.success(this.translate.instant('RECEIPT_DETAIL.TOAST_MERGE_SUCCESS'));
  }

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

  mergeItems(): void {
    this.mergedItemName = this.selectedItemsForMerge[0]?.name ?? '';
    this.mergeConfirmOpen.set(true);
  }

  toggleGroup(key: string): void {
    this.openGroups[key] = !this.openGroups[key];
  }

  startEditingCell(event: Event, cellKey: string): void {
    if (this.currentMode !== 'edit') return;
    event.stopPropagation();
    this.editingCell = cellKey;
  }

  assignAccountToItem(): void {
    const selectedAccount = this.selectedAccountsPerItem()[this.selectedItemForAccount()!];
    if (!selectedAccount) return;

    this.items.forEach(item => {
      if (item.id === this.selectedItemForAccount()) {
        item.accountValue = selectedAccount.number;
      }
    });

    this.closeDrawer();
    this.searchQuery.set('');
    this.selectedItemForAccount.set(null);
  }

  onAccountChange(itemId: number, account: Account): void {
    this.selectedAccountsPerItem.update(map => ({ ...map, [itemId]: account }));
  }

  openDrawer(item: any): void {
    this.selectedItemForAccount.set(item.id);
    this.selectedItemName = item.name;
    this.drawerOpen.set(true);

    this.onFetchAccounts().then(() => {
      if (!this.selectedAccountsPerItem()[item.id] && item.accountValue) {
        const matchingAccount = this.groupedAccounts()
          .flatMap((cls: any) => cls.categories)
          .flatMap((cat: any) => cat.accounts)
          .find((acc: any) => acc.number === item.accountValue);

        if (matchingAccount) {
          this.selectedAccountsPerItem.update(map => ({ ...map, [item.id]: matchingAccount }));
        }
      }
    });
  }

  closeDrawer(): void {
    this.drawerOpen.set(false);
  }

  openModal(): void {
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.modalOpen.set(false);
  }
}
