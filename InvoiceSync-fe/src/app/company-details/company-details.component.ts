import { Component } from '@angular/core';
import {initFlowbite} from 'flowbite'
import {NgForOf, NgIf} from "@angular/common";
import {AxiosService} from "../axios.service";

import { GroupedAccounts  } from "./chart-account.model"

@Component({
  selector: 'app-company-details',
  imports: [
    NgForOf,
    NgIf
  ],
  templateUrl: './company-details.component.html',
  styleUrl: './company-details.component.css'
})
export class CompanyDetailsComponent {

  constructor(private axiosService: AxiosService) {
  }


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
