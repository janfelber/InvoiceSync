import {Component, OnInit} from '@angular/core';
import {DecimalPipe, NgClass, NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {AxiosService} from "../axios.service";

@Component({
  selector: 'app-receipt-details',
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule,
    NgClass,
    DecimalPipe
  ],
  templateUrl: './receipt-details.component.html',
  styleUrl: './receipt-details.component.css'
})
export class ReceiptDetailsComponent implements OnInit {
  constructor(
    private axiosService: AxiosService,
    private route: ActivatedRoute
  ) {
  }

  receiptId: string = '';
  companyId: any = null;


  receipt: any = {};
  items: any = [];
  partner: any = {};
  myIdentity: any = {};

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.receiptId = params.get('id') || '';
    });
    this.onFetchReceipt();
    this.infoCompany();
  }

  //get total price of re

  async infoCompany() {
    try {
      const response = await this.axiosService.request(
        "GET",
        `/api/v1/company/info/3`,
        null
      );
      this.myIdentity = response.data;
    } catch (error) {
      console.error('Error fetching company info', error);
    }
  }

  onFetchReceipt() {
    this.axiosService.request(
      'GET',
      `/api/v1/receipt/${this.receiptId}`,
      null,
    ).then(response => {
      const data = response.data;
      console.log(data);
      this.companyId = data.company;
      this.receipt = data.receiptDetails;
      this.partner = data.partner;
      this.items = data.items;

    });
  }

}
