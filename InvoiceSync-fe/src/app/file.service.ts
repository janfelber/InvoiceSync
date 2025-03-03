import { HttpClient, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private server = 'http://localhost:8080'

  constructor(private http: HttpClient) {
  }

  //define a method to upload a file
  upload(formData: FormData): Observable<HttpEvent<string[]>> {
    const token = localStorage.getItem('token'); // Názov kľúča môže byť iný, záleží na tom, ako ho ukladáš
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<string[]>(`${this.server}/xml-file/upload`, formData, {
      headers: headers,
      reportProgress: true,
      observe: 'events'
    });
  }

  uploadReceipt(formData: FormData): Observable<HttpEvent<string[]>> {
    const token = localStorage.getItem('token'); // Názov kľúča môže byť iný, záleží na tom, ako ho ukladáš
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<string[]>(`${this.server}/api/v1/receipt/save`, formData, {
      headers: headers,
      reportProgress: true,
      observe: 'events'
    });
  }

  uploadPdf(formData: FormData): Observable<HttpEvent<string[]>> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<string[]>(`${this.server}/api/v1/import-invoice`, formData, {
      headers: headers,
      reportProgress: true,
      observe: 'events'
    });
  }

  uploadXml(formData: FormData) {
    return this.http.post(`${this.server}/xml-file/upload`, formData, {
      headers: new HttpHeaders(),
      observe: 'events',
      reportProgress: true
    });
  }

  //define a method to download a file
  download(filename: string) : Observable<HttpEvent<Blob>> {
    return this.http.get( `${this.server}/file/download/${filename}`, {
      reportProgress: true,
      observe: 'events',
      responseType: 'blob'
    });
  }
}
