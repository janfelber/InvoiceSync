import {Component, OnInit} from '@angular/core';
import {initFlowbite} from 'flowbite'
import {NgForOf, NgIf} from "@angular/common";
import {AxiosService} from "../axios.service";

import { GroupedAccounts  } from "./chart-account.model"
import {MatDialog} from "@angular/material/dialog";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {ActivatedRoute, Router} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CompanyService} from "../services/company.service";

@Component({
  selector: 'app-company-details',
  imports: [
    NgForOf,
    NgIf,
    FormsModule,
    MatDialogWindowComponent,
    ReactiveFormsModule
  ],
  templateUrl: './company-details.component.html',
  styleUrl: './company-details.component.css'
})
export class CompanyDetailsComponent implements OnInit{

  constructor(
    private axiosService: AxiosService,
    private route: ActivatedRoute,
    private companyService: CompanyService,
    private router: Router
    ) {
  }

  selectedClassIndex: number | null = null;
  subClasses: string[] = [];
  companyId : any = null
  company: any = {};
  editedCompany: any = {};
  loadingCompany = false;
  editCompanyModalOpen= false
  deleteCompanyModalOpen = false

  ngOnInit(): void {
    initFlowbite();
    this.onFetchChartAccounts0();
    this.onFetchChartAccounts1();
    this.onFetchChartAccounts2();
    this.onFetchChartAccounts3();
    this.onFetchChartAccounts4();
    this.onFetchChartAccounts5();
    this.onFetchChartAccounts6();
    this.onFetchChartAccounts7();

    this.route.paramMap.subscribe(params => {
      console.log(params.get('id'))
      this.companyId = params.get('id') || '';
      if (this.companyId) {
        this.onFetchCompany();
      } else {
        console.error('Company ID is missing!');
      }
    });
  }

  onFetchCompany() {
    this.loadingCompany = true;
    const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

    Promise.all([
      this.companyService.getCompanyById(
        {
          companyId: this.companyId
        }
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

  onClassSelect(index: number) {
    this.selectedClassIndex = index;
    console.log(this.selectedClassIndex)
    this.selectedClassName = this.classes[index];
  }

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

    this.companyService.updateCompany(
      {
        companyId:  this.company.id,
        company:    this.editedCompany
      }
      )
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
    this.companyService.deleteCompany(
      {
        companyId:this.companyId
      }
    )
      .then(() => {
        console.log('Zmazané, idem navigovať...');
        this.deleteCompanyModalOpen = false;
        this.router.navigate(['home']);
      })
      .catch(err => {
        console.error('Chyba pri mazaní:', err);
      });
  }

  protected readonly Object = Object;
  openCategories: Set<string> = new Set();
  protected accounts0: GroupedAccounts = {};
  protected accounts1: GroupedAccounts = {};
  protected accounts2: GroupedAccounts = {};
  protected accounts3: GroupedAccounts = {};
  protected accounts4: GroupedAccounts = {};
  protected accounts5: GroupedAccounts = {};
  protected accounts6: GroupedAccounts = {};
  protected accounts7: GroupedAccounts = {};

  toggleCategory(category: string): void {
    if (this.openCategories.has(category)) {
      this.openCategories.delete(category);
    } else {
      this.openCategories.add(category);
    }
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

  onFetchChartAccounts1() {
    const params = {
      classId: "1"
    };

    this.axiosService.request(
      'GET',
      `/api/v1/chart-account/categories`,
      null,
      { params }
    ).then(response => {
      this.accounts1 = response.data;
    });
  }

  onFetchChartAccounts2() {
    const params = {
      classId: "2"
    };

    this.axiosService.request(
      'GET',
      `/api/v1/chart-account/categories`,
      null,
      { params }
    ).then(response => {
      this.accounts2 = response.data;
    });
  }

  onFetchChartAccounts3() {
    const params = {
      classId: "3"
    };

    this.axiosService.request(
      'GET',
      `/api/v1/chart-account/categories`,
      null,
      { params }
    ).then(response => {
      this.accounts3 = response.data;
    });
  }

  onFetchChartAccounts4() {
    const params = {
      classId: "4"
    };

    this.axiosService.request(
      'GET',
      `/api/v1/chart-account/categories`,
      null,
      { params }
    ).then(response => {
      this.accounts4 = response.data;
    });
  }

  onFetchChartAccounts5() {
    const params = {
      classId: "5"
    };

    this.axiosService.request(
      'GET',
      `/api/v1/chart-account/categories`,
      null,
      { params }
    ).then(response => {
      this.accounts5 = response.data;
    });
  }

  onFetchChartAccounts6() {
    const params = {
      classId: "6"
    };

    this.axiosService.request(
      'GET',
      `/api/v1/chart-account/categories`,
      null,
      { params }
    ).then(response => {
      this.accounts6 = response.data;
    });
  }

  onFetchChartAccounts7() {
    const params = {
      classId: "7"
    };

    this.axiosService.request(
      'GET',
      `/api/v1/chart-account/categories`,
      null,
      { params }
    ).then(response => {
      this.accounts7 = response.data;
    });
  }

  onEdit() {
    console.log("edit")
  }
}
