import { HttpClient, HttpErrorResponse, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import {InvoiceRequest} from "../models/invoice-request";
import {AxiosService} from "../axios.service";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {

  private baseUrl = environment.apiUrl + "/v1/import";

  constructor(private http: HttpClient,    private axiosService: AxiosService) {
  }
    fetchAllInvoices(): Promise<any> {
      return this.axiosService.request("GET", `${this.baseUrl}/user`, null);
    }

    //fetch all invoices for current user
    fetchAllImports(userId: number): Observable<any[]> {
      return this.http.get<any[]>(`${this.baseUrl}/v1/import/user/1`);
    }

    //fetch company for current user
    fetchCompany() : Observable<any> {
      return this.http.get<any>( `${this.baseUrl}/v1/company/user/1
      `);
      }

    // Fetch XML content by import ID
  getXmlContent(importId: number): Observable<string> {
    return this.http.get(`${this.baseUrl}/file/get/xmlContent/${importId}`, { responseType: 'text' });
  }

  // downloadZip( import_id: number, company_id: number,): Observable<string> {
  //   return this.http.get(`${this.server}/file/download-zip/${import_id}/${company_id}`, { responseType: 'text' });
  // }

  getZipFile(importId: number): Observable<Blob> {
    const url = `${this.baseUrl}/file/generateZip/${importId}`;
    return this.http.get(url, {
      responseType: 'blob'
    });
  }

  //new methods
  exportPohodaInvoice(invoiceRequest: InvoiceRequest) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem("token")}`
    });

    return this.http.post(`http://localhost:8080/v1/pohoda/export/received`, invoiceRequest, {
      headers: headers,
      responseType: 'blob'
    });
  }

  fetchInvoicesByCompany(companyId: number) {
    return this.axiosService.request("GET", `${this.baseUrl}/company/${companyId}/current-user`, null)
  }

}
