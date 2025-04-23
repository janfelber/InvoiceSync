import {Component, Input, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {NgForOf, NgIf} from "@angular/common";
import {AxiosService} from "../axios.service";
import {initFlowbite} from "flowbite";

@Component({
  selector: 'app-account-class',
  imports: [
    NgIf,
    NgForOf,
    CommonModule
  ],
  templateUrl: './account-class.component.html',
  styleUrl: './account-class.component.css'
})
export class ChartAccountsComponent implements OnInit {

  classIds = ['0', '1', '2', '3', '4', '5', '6', '7'];
  activeClassId = '0'; // default active
  chartAccountData: any;
  openCategories: Set<string> = new Set();

  constructor(private axiosService: AxiosService) {}

  objectKeys = Object.keys;

  isCategoryOpenMap: { [category: string]: boolean } = {};

  toggleCategory(category: string): void {
    this.isCategoryOpenMap[category] = !this.isCategoryOpenMap[category];
  }

  isCategoryOpen(category: string): boolean {
    return !!this.isCategoryOpenMap[category];
  }

  ngOnInit(): void {
    initFlowbite();
    this.loadChartAccountData(this.activeClassId);  // Načítanie dát pre predvolený tab
  }

  loadChartAccountData(classId: string): void {
    this.activeClassId = classId;

    const params = { classId: classId };

    this.axiosService.request('GET', '/api/v1/chart-account/categories', null, { params })
      .then(response => {
        this.chartAccountData = response.data;
      })
      .catch(error => {
        console.error("Chyba pri načítavaní dát", error);
      });
  }

  protected readonly Object = Object;
}
