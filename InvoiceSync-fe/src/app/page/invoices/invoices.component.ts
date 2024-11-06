import { Component } from '@angular/core';
import { GridInvoicesComponent } from "../../components/grid-invoices/grid-invoices.component";
import {HeaderCompanyComponent} from "../../header-company/header-company.component";
import {FileService} from "../../file.service";
import {InvoiceService} from "./invoice.service";
import {AxiosService} from "../../axios.service";
import {HttpErrorResponse, HttpEvent, HttpEventType} from "@angular/common/http";
import saveAs from "file-saver";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {MatButton, MatIconButton} from "@angular/material/button";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";

@Component({
  selector: 'app-test',
  standalone: true,
  imports: [GridInvoicesComponent, HeaderCompanyComponent, DatePipe, MatButton, MatCell, MatCellDef, MatColumnDef, MatFormField, MatHeaderCell, MatHeaderRow, MatHeaderRowDef, MatIcon, MatIconButton, MatLabel, MatOption, MatRow, MatRowDef, MatSelect, MatTable, NgForOf, NgIf],
  templateUrl: './invoices.component.html',
  styleUrl: './invoices.component.css'
})
export class Invoices {

  protected filenames: string[] = [];

  protected fileStatus = { status: '', requestType: '', percentage: 0 };

  protected imports: string[] = [];

  protected companies: any[] = [];

  protected isLoading: boolean = true;

  protected displayedColumn: string[] = ['filename', "username", 'created_at', "download"];

  protected dataSource: any[] = [];

  protected selectedCompanyId: any;

  protected selectedCompanyName: string = '';

  protected schema_name: string = '';

  private user_id = this.axiosService.getUserId();

  constructor(private fileService: FileService, private invoiceService: InvoiceService, private axiosService: AxiosService) { }


  ngOnInit(): void {
    this.onFetchAllImports();
  }

  onFetchAllImports(): void {
    this.axiosService.request(
      "GET",
      `api/v1/import/user/${this.user_id}`,
      null
    ).then(
      (imports) => {
        this.imports = imports.data
        this.dataSource = imports.data;
        this.isLoading = false;
        console.log(imports.data);
      }
    )
  }

  /**
   * @deprecated This method is deprecated and will be removed in future versions this should use new api approach
   */
  onFetchImports(): void {
    this.isLoading = true;
    this.invoiceService.fetchImports(this.selectedCompanyId).subscribe(
      imports => {
        this.imports = imports;
        this.isLoading = false;
        this.dataSource = imports;
        console.table(imports);
      },
      error => {
        console.log(error);
        this.isLoading = false;
      }
    );
  }

  /**
   * @deprecated This method is deprecated and will be removed in future versions this should use new api approach
   */
  onFetchCompanies(): void {
    this.invoiceService.fetchCompany().subscribe(
      companies => {
        this.companies = companies;
        console.table(companies);
      },
      error => {
        console.log(error);
      }
    )
  }

  downloadFile(importId: number) {
    this.axiosService.request('GET', `/file/generateZip/${importId}`, null, { responseType: 'blob' })
      .then((response) => {
        console.log(response);
        const blob = new Blob([response.data]);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'output.zip';
        a.click();
        window.URL.revokeObjectURL(url);
      })
      .catch(error => {
        console.error('Download error:', error);
      });
  }

  /**
   * @deprecated This method is deprecated and will be removed in future versions this should use new api approach
   */
  public onUploadFiles(files: File[]): void {
    const formData = new FormData();
    for (const file of files) { formData.append('file', file, file.name); }
    this.fileService.upload(formData).subscribe(
      event => {
        console.log(event);
        this.reportProgress(event);
      },
      (error: HttpErrorResponse) => {
        console.error(error);
      }
    );
  }

  private reportProgress(httpEvent: HttpEvent<string[] | Blob>): void {
    switch (httpEvent.type) {
      case HttpEventType.UploadProgress:
        this.updateStatus(httpEvent.loaded, httpEvent.total!, 'Uploading... ');
        break;
      case HttpEventType.DownloadProgress:
        this.updateStatus(httpEvent.loaded, httpEvent.total!, 'Downloading... ');
        break;
      case HttpEventType.ResponseHeader:
        console.log('Header returned', httpEvent);
        break;
      case HttpEventType.Response:
        if (httpEvent.body instanceof Array) {
          this.fileStatus.status = 'done';
          for (const filename of httpEvent.body) {
            this.filenames.unshift(filename);
          }
        } else {
          saveAs(new File([httpEvent.body!], httpEvent.headers.get('File-Name')!,
            { type: `${httpEvent.headers.get('Content-Type')};charset=utf-8` }));
        }
        this.fileStatus.status = 'done';
        break;
      default:
        console.log(httpEvent);
        break;

    }
  }

  private updateStatus(loaded: number, total: number, requestType: string) {
    this.fileStatus.status = 'progress';
    this.fileStatus.requestType = requestType;
    this.fileStatus.percentage = Math.round(100 * loaded / total);
  }

  openFileDialog() {
    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    inputElement.accept = '.xml';

    inputElement.click();

    inputElement.addEventListener('change', (event: Event) => {
      const target = event.target as HTMLInputElement;
      if (target.files) {
        this.onUploadFiles(Array.from(target.files));
        alert('Files selected');
      }
    });
  }

  onCompanyChange(company: any) {
    this.selectedCompanyId = company.company_id;
    this.selectedCompanyName = company.name;
    this.schema_name = company.schema_name;

    // Zavolanie funkcie na získanie importov
    this.onFetchImports();
  }


  reloadImports() {
    this.onFetchImports();
  }

}
