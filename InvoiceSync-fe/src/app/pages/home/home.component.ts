import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import { MatDialogWindowComponent } from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {ToastrService} from "ngx-toastr";
import {Router, RouterLink} from "@angular/router";
import {CompanyService} from "../../core/services/company.service";
import {PageResponseCompanyResponseDto} from "../../core/models/page-response-company-response-dto";
import {CompanyRequest} from "../../core/models/company-request";
import {TranslateModule} from "@ngx-translate/core";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css'],
  imports: [
    FormsModule,
    NgForOf,
    MatDialogWindowComponent,
    RouterLink,
    ReactiveFormsModule,
    NgIf,
    TranslateModule,
  ]
})
export class HomeComponent implements OnInit{

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private companyService: CompanyService,
    private toastr: ToastrService) {
  }

  companyForm!: FormGroup;
  modalOpen = false;
  filterOpen = false;
  filterCity = '';
  registrationNumber: string = '';

  companyResponse: PageResponseCompanyResponseDto = {
    content: []
  };

  protected readonly Math = Math;
  searchText: string = '';
  page: number = 0;
  size: number = 10;

  activeTabValue: 'by_registration' | 'manual' = 'by_registration';

  set activeTab(tab: 'by_registration' | 'manual') {
    this.activeTabValue = tab;
  }

  get activeTab(): 'by_registration' | 'manual' {
    return this.activeTabValue;
  }

  getInitials(name: string): string {
    if (!name) return '';
    const words = name.trim().split(' ');
    if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
    return (words[0][0] + words[1][0]).toUpperCase();
  }

  ngOnInit(): void {
    this.onFetchAllCompanies();
    this.initForm();
  }

  initForm(): void {
    this.companyForm = this.fb.group({
      name: ['', Validators.required],
      city: [''],
      registrationNumber: [''],
      street: [''],
      streetNumber: [''],
      taxId: [''],
      vatId: [''],
      zip: ['']
    });
  }

  saveCompany(): void {
    if (this.activeTab === 'by_registration') {
      this.companyService.saveCompanyByRegistrationNumber(this.registrationNumber
      ).then(() => {
        this.toastr.success(
          'Spoločnosť s IČO: <b>' + this.registrationNumber + '</b> úspešne vytvorená',
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
        this.onFetchAllCompanies();
        this.registrationNumber = '';
        this.modalOpen = false;
      })
    }
    else if (this.activeTab === 'manual') {
      if (this.companyForm.valid) {
        const companyRequest: CompanyRequest = this.companyForm.value;
        this.companyService.saveCompany(companyRequest)
          .then(() => {
            this.toastr.success(
              'Spoločnosť <b>' + this.companyForm.value.name + '</b> úspešne vytvorená',
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
            this.onFetchAllCompanies();
            this.companyForm.reset();
            this.modalOpen = false;
          })
          .catch((error) => {
            console.error("Chyba pri ukladaní spoločnosti:", error);
            this.toastr.error('Nepodarilo sa uložiť spoločnosť.');
          });
      } else {
        this.toastr.warning('Vyplňte prosím všetky povinné polia.');
      }
    }
  }

  onFetchAllCompanies(): void {
    this.companyService.findAllCompaniesByUser({
      page: this.page,
      size: this.size
    })
      .then(response => {
        this.companyResponse = response.data;
        console.log(this.companyResponse.content)
      })
      .catch(error => {
        console.error('Error while fetching companies', error);
      });
  }

  applyFilter() {
    this.page = 0;
    this.onFetchAllCompanies();
  }

  resetFilter() {
    this.filterCity = '';
    this.page = 0;
    this.onFetchAllCompanies();
  }

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  goToPreviousPage() {
    this.page--;
    this.onFetchAllCompanies();
  }

  goToPage(page: number) {
    this.page = page;
    this.onFetchAllCompanies();
  }

  goToNextPage() {
    this.page++;
    this.onFetchAllCompanies();
  }

  get IsLastPage(): boolean {
    return this.page == this.companyResponse.totalPages as number - 1
  }
}
