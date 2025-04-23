import { Component } from '@angular/core';
import {initFlowbite} from 'flowbite'
import {NgForOf, NgIf} from "@angular/common";
import {AxiosService} from "../axios.service";

import { GroupedAccounts  } from "./chart-account.model"
import {MatDialog} from "@angular/material/dialog";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {ActivatedRoute, Router} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CompanyService} from "../services/company.service";
import { ChartAccountsComponent} from "../account-class/account-class.component";

@Component({
  selector: 'app-company-details',
  imports: [
    NgIf,
    FormsModule,
    MatDialogWindowComponent,
    ReactiveFormsModule,
    ChartAccountsComponent
  ],
  templateUrl: './company-details.component.html',
  styleUrl: './company-details.component.css'
})
export class CompanyDetailsComponent {

  constructor(
    private axiosService: AxiosService,
    private route: ActivatedRoute,
    private companyService: CompanyService,
    private router: Router
    ) {
  }

  ngOnInit(): void {
    initFlowbite();

    this.route.paramMap.subscribe(params => {
      this.companyId = params.get('id') || '';
    });

    this.onFetchCompany();
  }


  selectedClassIndex: number | null = null;
  companyId: any = null;
  company: any = {};
  editedCompany: any = {};
  loadingCompany = false;
  editCompanyModalOpen= false
  deleteCompanyModalOpen = false


  selectedClassName = 'Učtová trieda';
  protected classes =
    [
    'DLHODOBÝ MAJETOK',
    'MAJETOK',
    'INANČNÉ ÚČTY',
    'ZÚČTOVACIE VZŤAHY',
    'KAPITÁLOVÉ ÚČTY A DLHODOBÉ ZÁVÄZKY',
    'NÁKLADY',
    'VÝNOSY',
    'UZÁVIERKOVÉ ÚČTY A PODSÚVAHOVÉ ÚČTY'
    ]

  openEditModal() {
    this.editedCompany = { ...this.company };
    this.editCompanyModalOpen = true
  }

  closeEditModal() {
    this.editCompanyModalOpen = false
  }

  openDeleteModal() {
    this.deleteCompanyModalOpen = true
  }

  closeDeleteModal() {
    this.deleteCompanyModalOpen = false
  }

  editCompany(): void {
    if (!this.company?.id) return;

    this.companyService.updateCompany(this.company.id, this.editedCompany)
      .then((updatedCompany) => {
        this.company = updatedCompany;
        console.log(this.editedCompany)
        this.editCompanyModalOpen = false;
        this.onFetchCompany()
      })
      .catch((error) => {
        console.error("Chyba pri aktualizácii spoločnosti", error);
      });
  }

  deleteCompany():void {
    this.companyService.deleteCompany(this.company.id)
      .then(() => {
        console.log('Zmazané, idem navigovať...');
        this.deleteCompanyModalOpen = false;
        this.router.navigate(['home']);
      })
      .catch(err => {
        console.error('Chyba pri mazaní:', err);
      });
  }

  onFetchCompany() {
    this.loadingCompany = true;

    const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

    Promise.all([
      this.axiosService.request(
        'GET',
        `/api/v1/company/info/${this.companyId}`,
        null
      ),
      delay(250)
    ])
      .then(([response]) => {
        this.company = response.data;
      })
      .finally(() => {
        this.loadingCompany = false;
      });
  }

  protected readonly Object = Object;
  openCategories: Set<string> = new Set();
  protected accounts0: GroupedAccounts = {};

  toggleCategory(category: string): void {
    if (this.openCategories.has(category)) {
      this.openCategories.delete(category);
    } else {
      this.openCategories.add(category);
    }
  }

  accounts = {
    0: this.accounts0,
  }


  get accountKeys() {
    return Object.keys(this.accounts);
  }

  isCategoryOpen(category: string): boolean {
    return this.openCategories.has(category);
    }

  onFetchChartAccounts0() {
    const params = {
      classId: "0"
    };

    this.axiosService.request(
      'GET',
      `/api/v1/chart-account/categories`,
      null,
      { params }
    ).then(response => {
      this.accounts0 = response.data;
    });
  }

  onEdit() {
    console.log("edit")
  }
}
