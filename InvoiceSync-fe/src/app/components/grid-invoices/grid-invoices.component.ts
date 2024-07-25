import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FileService } from '../../file.service';
import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import saveAs from 'file-saver';
import { InvoiceService } from './invoice.service';
import { MatTableModule } from '@angular/material/table';
import {MatSelectModule} from '@angular/material/select';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDividerModule} from '@angular/material/divider';
import {MatListModule} from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';



@Component({
  selector: 'app-grid-invoices',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatSelectModule,
    MatFormFieldModule,
    MatDividerModule,
    MatListModule,
    MatToolbarModule,
    FormsModule,
    HttpClientModule
  ],
  templateUrl: './grid-invoices.component.html',
  styleUrls: ['./grid-invoices.component.css']
})
export class GridInvoicesComponent {
  filenames: string[] = [];
  fileStatus = { status: '', requestType: '', percentage: 0 };
  


  constructor(private fileService: FileService, private invoiceService: InvoiceService) {}

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
    switch(httpEvent.type) {
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
                  {type: `${httpEvent.headers.get('Content-Type')};charset=utf-8`}));
        }
        this.fileStatus.status = 'done';
        break;
        default:
          console.log(httpEvent);
          break;
      
    }
  }

  private updateStatus(loaded: number, total: number , requestType: string) {
    this.fileStatus.status = 'progress';
    this.fileStatus.requestType = requestType;
    this.fileStatus.percentage = Math.round(100 * loaded / total);
  }

  public test() {
    alert('test');
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

  
  imports: any[] = [];
  companies: any[] = [];

  isLoading: boolean = true;
  public displayedColumn: string[] = [ 'filename', "username", 'created_at',"download" ];
  public dataSource: any[] = [];
ngOnInit(): void {
      this.onFetchAllImports();
      this.onFetchCompanies()
  }

  selectedCompanyId: any ;
  selectedCompanyName: string = '';
  schema_name: string = '';
  
  onCompanyChange(company: any) {
    this.selectedCompanyId = company.company_id;
    this.selectedCompanyName = company.name;
    this.schema_name = company.schema_name;
    
    // Zavolanie funkcie na získanie importov
    this.onFetchImports();
  }
  
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

  onFetchAllImports(): void {
    this.invoiceService.fetchAllImports(1).subscribe(
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

  reloadImports() {
    this.onFetchImports();
  }

  onFetchCompanies() : void {
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
    this.invoiceService.getZipFile(importId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'output.zip'; // Set default file name
      a.click();
      window.URL.revokeObjectURL(url);
    }, error => {
      console.error('Download error:', error);
    });
  }


  }

  // downloadZip(import_id: number, company_id: number) {
  //   this.invoiceService.downloadZip(import_id, company_id, ).subscribe((zipContent) => {
  //     console.log(zipContent); // Vypíše hodnotu zip_content do konzoly
  //   });
  // }

  // // click on element
  // onClickElement(element: any) {
  //   console.log(element);
  // }

  

