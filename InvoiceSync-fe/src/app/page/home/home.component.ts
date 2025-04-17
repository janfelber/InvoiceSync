import {Component, ElementRef, ViewChild} from '@angular/core';
import { NgForOf } from "@angular/common";
import {FormsModule} from "@angular/forms";
import {AxiosService} from "../../axios.service";
import { MatDialogWindowComponent } from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {ToastrService} from "ngx-toastr";
import {RouterLink} from "@angular/router";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css'],
  imports: [
    FormsModule,
    NgForOf,
    MatDialogWindowComponent,
    RouterLink,
  ]
})
export class HomeComponent {

  constructor(
    private axiosService: AxiosService,
    private toastr: ToastrService) {
  }

  @ViewChild('successToast') successToast!: ElementRef;

  columnWidths = ['30%', '5%'];
  headers = ['Názov spoločnosti', 'Mesto', 'IČO', 'IČ DPH'];
  currentPage = 1;
  rowsPerPage = 10;
  currentPageInput = 1;
  pageSizes = [5,10, 20, 50];
  filteredCompanies: any[] = [];
  searchText: string = '';

  ngOnInit(): void {
    this.onFetchAllCompanies();
  }

  modalOpen = false;

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  searchCompany(): void {
    this.filteredCompanies = this.filteredCompanies.filter(company =>
    company.name.toLowerCase().includes(this.searchText.toLowerCase())
    )
  }

  resetSearch(): void {
    this.searchText = '';
    this.onFetchAllCompanies();
  }

  formData = {
    name: '',
    city: '',
    street: '',
    streetNumber: '',
    zip: '',
    registrationNumber: '',
    taxId: '',
    vatId: ''
  };

  createNewCompany(): void {
    console.log(this.formData)
    this.axiosService.request(
      "POST",
      `/api/v1/company/add`,
      this.formData
    ).then(() => {
      this.modalOpen = false;
      this.onFetchAllCompanies();

      this.toastr.success(
        'Spoločnosť <b>' + this.formData.name + '</b> úspešne vytvorená',
        '',
        {
          timeOut: 3000,
          progressBar: true,
          progressAnimation: 'increasing',
          closeButton: true,
          positionClass: 'toast-top-right',
          enableHtml: true,
        }
      );

      this.formData = {
        name: '',
        city: '',
        street: '',
        streetNumber: '',
        zip: '',
        registrationNumber: '',
        taxId: '',
        vatId: ''
      };


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
