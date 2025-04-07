import {Component, OnInit} from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AxiosService } from "../axios.service";
import { NgForOf, NgIf } from "@angular/common";
import { ToastrService } from 'ngx-toastr';
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
    selector: 'app-header-company',
    imports: [
        MatToolbarModule,
        NgForOf,
        NgIf
    ],
    templateUrl: './header-company.component.html',
    styleUrl: './header-company.component.css'
})
export class HeaderCompanyComponent implements OnInit {

  constructor(
    private axiosService: AxiosService,
    private toastr: ToastrService,
    private dialog: MatDialog
  ) {
  }

  protected companies: any[] = [];
  isLoading = true;
  selectedCompanyName = 'Vyber spoločnosť';

  ngOnInit() {
    this.onFetchCompanies();
  }

  openDialog() {
    const dialogRef = this.dialog.open(MatDialogWindowComponent, {
      width: '400px',
      panelClass: 'custom-dialog-container',
      data: {
        title: 'Nova spolocnost',
        inputs: [
          { label: 'Názov', type: 'text', placeholder: '', value: '', required: true },
        ],
        confirmText: 'Uložiť',
        cancelText: 'Zrušiť'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.saveCompany(result);
      } else {
        console.log('Dialóg bol zrušený');
      }
    });
  }

  saveCompany(data: any): void {
    const valuesToSend = data.map((input: { value: any }) => input.value);
    this.axiosService.request(
      "POST",
      `/v1/company/add`,
      { name: valuesToSend[0] }
    ).then(response => {
      this.onFetchCompanies();
    }).catch(error => {
      console.error('Chyba pri vytváraní spoločnosti:', error);
    });
  }

  //select company from dropdown
  onSelectCompany(company: any) {
    this.selectedCompanyName = company.name;
  }

  onFetchCompanies() {
      this.axiosService.request(
        "GET",
        `/v1/company/user`,
        null
      ).then(
        (companies) => {
          this.companies = companies.data
          this.isLoading = false;

          if (this.companies.length === 0) {
            this.toastr.warning('Nemáte pridané žiadne spoločnosti!', 'Informácia',
              {
                timeOut: 3000,
                progressBar: true,
                progressAnimation: 'increasing',
                closeButton: true,
                positionClass: 'toast-top-right'
              });
          }
        }
      ).catch(() => {
        this.isLoading = false;
      });
  }


}
