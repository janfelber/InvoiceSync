import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FileService } from '../../file.service';
import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { InvoiceService } from '../../page/invoices/invoice.service';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AxiosService } from '../../axios.service';



@Component({
    selector: 'app-grid-invoices',
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
        // TODO: `HttpClientModule` should not be imported into a component directly.
        // Please refactor the code to add `provideHttpClient()` call to the provider list in the
        // application bootstrap logic and remove the `HttpClientModule` import from this component.
        // TODO: `HttpClientModule` should not be imported into a component directly.
        // Please refactor the code to add `provideHttpClient()` call to the provider list in the
        // application bootstrap logic and remove the `HttpClientModule` import from this component.
        // TODO: `HttpClientModule` should not be imported into a component directly.
        // Please refactor the code to add `provideHttpClient()` call to the provider list in the
        // application bootstrap logic and remove the `HttpClientModule` import from this component.
        // TODO: `HttpClientModule` should not be imported into a component directly.
        // Please refactor the code to add `provideHttpClient()` call to the provider list in the
        // application bootstrap logic and remove the `HttpClientModule` import from this component.
        HttpClientModule
    ],
    templateUrl: './grid-invoices.component.html',
    styleUrls: ['./grid-invoices.component.css']
})
export class GridInvoicesComponent {
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

  constructor(private fileService: FileService, private invoiceService: InvoiceService, private axiosService: AxiosService) { }


  ngOnInit(): void {
    this.onFetchAllImports();
  }

  onFetchAllImports(): void {
    this.axiosService.request(
      "GET",
      "api/v1/import/user/1",
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
      },
      (error: HttpErrorResponse) => {
        console.error(error);
      }
    );
  }

  // private reportProgress(httpEvent: HttpEvent<string[] | Blob>): void {
  //   switch (httpEvent.type) {
  //     case HttpEventType.UploadProgress:
  //       this.updateStatus(httpEvent.loaded, httpEvent.total!, 'Uploading... ');
  //       break;
  //     case HttpEventType.DownloadProgress:
  //       this.updateStatus(httpEvent.loaded, httpEvent.total!, 'Downloading... ');
  //       break;
  //     case HttpEventType.ResponseHeader:
  //       console.log('Header returned', httpEvent);
  //       break;
  //     case HttpEventType.Response:
  //       if (httpEvent.body instanceof Array) {
  //         this.fileStatus.status = 'done';
  //         for (const filename of httpEvent.body) {
  //           this.filenames.unshift(filename);
  //         }
  //       } else {
  //         saveAs(new File([httpEvent.body!], httpEvent.headers.get('File-Name')!,
  //           { type: `${httpEvent.headers.get('Content-Type')};charset=utf-8` }));
  //       }
  //       this.fileStatus.status = 'done';
  //       break;
  //     default:
  //       console.log(httpEvent);
  //       break;
  //
  //   }
  // }

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

