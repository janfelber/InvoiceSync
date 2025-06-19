import { HttpClient, HttpErrorResponse, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import {InvoiceRequest} from "../../core/models/invoice-request";

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private server = 'http://localhost:8080'

  constructor(private http: HttpClient) {
  }

    //define a method to fetch imports based on company id
    fetchImports(companyId: number): Observable<any[]> {
      return this.http.get<any[]>(`${this.server}/api/v1/import/user/1/company/${companyId}`);
    }

    //fetch all invoices for current user
    fetchAllImports(userId: number): Observable<any[]> {
      return this.http.get<any[]>(`${this.server}/api/v1/import/user/1`);
    }

    //fetch company for current user
    fetchCompany() : Observable<any> {
      return this.http.get<any>( `${this.server}/api/v1/company/user/1
      `);
      }

    // Fetch XML content by import ID
  getXmlContent(importId: number): Observable<string> {
    return this.http.get(`${this.server}/file/get/xmlContent/${importId}`, { responseType: 'text' });
  }

  // downloadZip( import_id: number, company_id: number,): Observable<string> {
  //   return this.http.get(`${this.server}/file/download-zip/${import_id}/${company_id}`, { responseType: 'text' });
  // }

  getZipFile(importId: number): Observable<Blob> {
    const url = `${this.server}/file/generateZip/${importId}`;
    return this.http.get(url, {
      responseType: 'blob'
    });
  }

  //new methods
  exportPohodaInvoice(invoiceRequest: InvoiceRequest) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem("token")}`
    });

    return this.http.post(`http://localhost:8080/api/v1/pohoda/export/received`, invoiceRequest, {
      headers: headers,
      responseType: 'blob'
    });
  }

  updateReceipt(invoiceRequest: InvoiceRequest, receiptId: number) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem("token")}`
    });

    return this.http.post(`http://localhost:8080/receipt/update/${receiptId}`, invoiceRequest);
  }

  exportPohodaReceipt(invoiceRequest: InvoiceRequest) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem("token")}`
    });

    return this.http.post(`http://localhost:8080/api/v1/receipt/pohoda/export/receipt`, invoiceRequest, {
      headers: headers,
      responseType: 'blob'
    });
  }








}
