import { Component } from '@angular/core';
import {NgClass, NgForOf, NgStyle} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {AxiosService} from "../../axios.service";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogWindowComponent } from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {ToastrService} from "ngx-toastr";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css'],
    imports: [
        FormsModule,
        NgForOf,
        NgClass,
        MatIcon,
        MatIconButton,
        NgStyle
    ]
})
export class HomeComponent {

  constructor(private axiosService: AxiosService, private dialog: MatDialog, private toastr: ToastrService) {
  }

  columnWidths = ['30%', '5%'];
  headers = ['Nazov', 'Akcie'];
  currentPage = 1;
  rowsPerPage = 10;
  currentPageInput = 1;
  pageSizes = [5,10, 20, 50];
  filteredCompanies: any[] = [];

  ngOnInit(): void {
    this.onFetchAllCompanies();
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
        console.log('Dialóg zatvorený s výsledkom:', result);
        this.saveCompany(result);
        this.toastr.success('Spoločnosť bola úspešne vytvorená!', 'Informácia',
          {
            timeOut: 3000,
            progressBar: true,
            progressAnimation: 'increasing',
            closeButton: true,
            positionClass: 'toast-top-right'
          });
      } else {
        console.log('Dialóg bol zrušený');
      }
    });
  }

  saveCompany(data: any): void {
    const valuesToSend = data.map((input: { value: any }) => input.value);

    console.log('Posielam údaje na server:', valuesToSend);

    this.axiosService.request(
      "POST",
      `/api/v1/company/add`,
      { name: valuesToSend[0] }
    ).then(response => {
      console.log('Spoločnosť bola úspešne vytvorená:', response);
      this.onFetchAllCompanies();
    }).catch(error => {
      console.error('Chyba pri vytváraní spoločnosti:', error);
    });
  }

  onFetchAllCompanies(): void {
    this.axiosService.request(
      "GET",
      `/api/v1/company/user`,
      null
    ).then(
      (comapanies) => {
        this.filteredCompanies = [...comapanies.data];
        console.log(comapanies.data);
      }
    )
  }

  get paginatedCompanies(): any[] {
    const start = (this.currentPage - 1) * this.rowsPerPage;
    const end = start + this.rowsPerPage;
    return this.filteredCompanies.slice(start, end);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredCompanies.length / this.rowsPerPage);
  }

  get recordRange(): string {
    const startRecord = (this.currentPage - 1) * this.rowsPerPage + 1;
    const endRecord = Math.min(this.currentPage * this.rowsPerPage, this.filteredCompanies.length);
    return `${startRecord} - ${endRecord} z ${this.filteredCompanies.length}`;
  }

  goToPage(page: number): void {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.currentPageInput = page;
    }
  }

  jumpToPage(): void {
    if (this.currentPageInput > 0 && this.currentPageInput <= this.totalPages) {
      this.goToPage(this.currentPageInput);
    }
  }
}
