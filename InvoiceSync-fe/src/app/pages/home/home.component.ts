import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { NgForOf } from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import { MatDialogWindowComponent } from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {ToastrService} from "ngx-toastr";
import {Router, RouterLink} from "@angular/router";
import {CompanyService} from "../../core/services/company.service";
import {PageResponseCompanyResponseDto} from "../../core/models/page-response-company-response-dto";
import {initFlowbite} from 'flowbite'
import {CompanyRequest} from "../../core/models/company-request";

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
  ]
})
export class HomeComponent implements OnInit{

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private companyService: CompanyService,
    private toastr: ToastrService) {
  }

  @ViewChild('successToast') successToast!: ElementRef;
  companyForm!: FormGroup;
  modalOpen = false;

  companyResponse: PageResponseCompanyResponseDto = {
    content: []
  };

  searchText: string = '';
  page: number = 0;
  size: number = 5;

  headers = ['Názov spoločnosti', 'Mesto', 'IČO', 'IČ DPH'];

  ngOnInit(): void {
    this.onFetchAllCompanies();
    initFlowbite();
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

        // Presmeruj podľa typu chyby
        if (error?.response?.status === 401) {
          this.router.navigate(['/unauthorized']);
        } else if (error?.response?.status === 403) {
          this.router.navigate(['/forbidden']);
        } else {
          this.router.navigate(['/error']);
        }
      });
  }

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }
}
