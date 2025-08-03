import {Component, OnInit} from '@angular/core';
import {initFlowbite} from 'flowbite';
import {DataTransferService} from "../../core/services/data-transfer.service";
import {ActivatedRoute} from "@angular/router";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'data-transfer-excel-inspect',
  imports: [
    NgForOf,
    NgIf
  ],
  templateUrl: './data-transfer-excel-inspect.component.html',
  styleUrl: './data-transfer-excel-inspect.component.css'
})
export class DataTransferExcelInspectComponent implements OnInit {

  constructor(
    private convertService: DataTransferService,
    private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.convertId = params.get('id') || '';
    });
    initFlowbite();
    this.onFetchExcelData();
  }

  convertId: any = null
  excelSheet: any[] = [];
  excelColumns: string[] = [];

  onFetchExcelData() {
    this.convertService.getConvertById({
      convertId: this.convertId
    })
      .then(response => {
        this.excelSheet = response.data.data

        this.excelSheet = JSON.parse(response.data.data);

        if (this.excelSheet.length > 0) {
          this.excelColumns = Object.keys(this.excelSheet[0]);
        }
      })
  }
}
