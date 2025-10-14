import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {accountsTutorialsByApp} from "../../../shared/tutorials/ExportAccoutsTutorial";
import {PostingAccountService} from "../../../core/services/posting-account.service";
import {PostingAccountRequest} from "../../../core/models/PostingAccountRequest";
import {PageResponsePostingAccount} from "../../../pages/home/page-response-posting-account";
import { initTooltips } from 'flowbite';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-company-internal-documents-accounts',
  imports: [
    FormsModule,
    MatDialogWindowComponent,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './company-internal-documents-accounts.component.html',
  styleUrl: './company-internal-documents-accounts.component.css'
})
export class CompanyInternalDocumentsAccountsComponent implements OnInit, AfterViewInit{
  @Input() companyId!: any | null;

  ngAfterViewInit(): void {
    setTimeout(() => initTooltips(), 100);
  }

  isUploading = false;
  importFinished = false;
  accountsImportModalOpen = false;
  showDetails = false;
  isTutorialOpen = false;

  externalFile: File | null = null;

  public accountResponse: PageResponsePostingAccount = {
    content: [],
  }

  public page: number = 0;
  public size: number = 15;

  accounts: any = []
  addedAccounts: { accountId: string; accountName: string }[] = [];

  activeTabValue: 'create' | 'importExternal' = 'create';

  autoClassId = '';
  autoClassName = '';
  autoCategoryId = '';
  autoCategoryName = '';
  newAccount: PostingAccountRequest = {
    companyId: 0,
    accountId: '',
    accountName: '',
    type: 'INTERNAL',
  }

  accountsTutorialsByApp = accountsTutorialsByApp;
  availableApps = Object.keys(this.accountsTutorialsByApp);

  constructor(
    private postAccountService: PostingAccountService,
    private toastr: ToastrService,
  ) {
  }

  set activeTab(tab: 'create' | 'importExternal') {
    this.activeTabValue = tab;
    if (tab === 'create') {
      this.externalFile = null
    } else {
      this.externalFile = null;
    }
  }

  get activeTab(): 'create' | 'importExternal' {
    return this.activeTabValue;
  }

  ngOnInit(): void {
        this.onFetchAccounts()
    initTooltips();
    }

  onFetchAccounts() {
    this.postAccountService.findAccountsByCompany({
      page: this.page,
      size: this.size,
      type: 'INTERNAL',
      companyId: this.companyId,
    }).then(response => {
      this.accountResponse = response.data;
      console.log(this.accountResponse)
    })
  }

  onAccountIdChange() {
    const id = this.newAccount.accountId;
    if (id?.length >= 3) {
      const prefix = id.substring(0, 3);
      this.autoCategoryId = prefix;
      this.autoCategoryName = this.mapCategoryName(prefix);

      this.autoClassId = prefix.charAt(0);
      this.autoClassName = this.mapClassName(this.autoClassId);
    } else {
      this.autoCategoryId = this.autoCategoryName = this.autoClassId = this.autoClassName = '';
    }
  }

  mapClassName(classId: string): string {
    const map: Record<string, string> = {
      '0': 'Dlhodobý majetok',
      '1': 'Zásoby',
      '2': 'Finančné účty',
      '3': 'Zúčtovacie vzťahy',
      '4': 'Kapitálové účty',
      '5': 'Náklady',
      '6': 'Výnosy',
      '7': 'Uzávierkové účty',
      '8': 'Vnútropodnikové účty',
      '9': 'Ostatné účty'
    }
    return map[classId] || '';
  }

  mapCategoryName(categoryId: string): string {
    const map: Record<string, string> = {
      '501': 'Spotreba materiálu',
      '343': 'Daň z pridanej hodnoty',
      '602': 'Tržby za služby',
      '321': 'Dodávatelia',
    }

    return map[categoryId] || '';
  }

  openAccountsImportModal() {
    this.accountsImportModalOpen = true;
  }

  closeAccountsImportModal() {
    this.accountsImportModalOpen = false;
  }

  openTutorialModal() {
    this.accountsImportModalOpen = false
    this.isTutorialOpen = true
  }

  closeTutorialModal() {
    this.isTutorialOpen = false
  }

  async onCreateAccounts(): Promise<void> {
    this.isUploading = true;
    this.importFinished = false;
    try {
      console.log('Active Tab:', this.activeTab);
      if (this.activeTab === 'importExternal') {
        if (this.externalFile) {
          const imported = await this.postAccountService.importChartOfAccounts({
            files: [this.externalFile],
            companyId: this.companyId
          });

          this.addedAccounts = imported.data ?? [];
          console.log(this.addedAccounts)
        }
      }
      if (this.activeTab === 'create') {
        this.newAccount.companyId = this.companyId;
        await this.postAccountService.savePostingAccount(this.newAccount)

      }
    } catch (err) {
      console.error(err);
    } finally {
      this.isUploading = false;
      this.importFinished = true;
    }
  }

  onExternalFileUpload(event: any): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.externalFile = input.files[0];
    }
  }

  //TODO refactor delete
  async onDeleteAccount(id: number, editable: boolean) {

    if (!editable) {
      this.toastr.error('Pre správne fungovanie systému nie je možné vymazať základný účtovný účet.', '', {
        timeOut: 5000,
        progressBar: true,
        progressAnimation: 'increasing',
        closeButton: true,
        positionClass: 'toast-top-right',
      });
      return;
    }

    this.postAccountService.deleteAccount({
      accountId: id
    })
  }

  goToPreviousPage() {
    this.page--;
    this.onFetchAccounts();
  }

  goToPage(page: number) {
    this.page = page;
    this.onFetchAccounts();
  }

  goToNextPage() {
    this.page++;
    this.onFetchAccounts();
  }

  get IsLastPage(): boolean {
    return this.page == this.accountResponse.totalPages as number - 1
  }

  protected readonly Math = Math;
}
