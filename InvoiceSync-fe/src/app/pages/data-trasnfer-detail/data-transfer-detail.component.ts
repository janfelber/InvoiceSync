import {Component, OnInit} from '@angular/core';
import {initFlowbite} from "flowbite";
import {DataTransferMappingComponent} from "../data-transfer-mapping/data-transfer-mapping.component";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {DataTransferExcelInspectComponent} from "../data-transfer-excel-inspect/data-transfer-excel-inspect.component";

@Component({
  selector: 'data-transfer-detail',
  imports: [
    DataTransferMappingComponent,
    RouterLink,
    DataTransferExcelInspectComponent
  ],
  templateUrl: './data-transfer-detail.component.html',
  styleUrl: './data-transfer-detail.component.css'
})
export class DataTransferDetailComponent implements OnInit{

  constructor(private route: ActivatedRoute) {}

    ngOnInit(): void {
        initFlowbite();
      this.route.queryParamMap.subscribe(params => {
        this.fileName = params.get('name');
      });
    }

  fileName: any = null;

}
