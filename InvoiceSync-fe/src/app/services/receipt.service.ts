import {environment} from "../../environments/environment";
import {Injectable} from "@angular/core";
import {AxiosService} from "../axios.service";
import {Observable} from "rxjs";
import {HttpClient, HttpEvent, HttpHeaders} from "@angular/common/http";


@Injectable({
  providedIn: 'root'
})
export class ReceiptService {

  private baseUrl = environment.apiUrl + "/v1/receipt";

  constructor(
    private axiosService: AxiosService,
    private http: HttpClient
  ) { }

  fetchAllReceipts(): Promise<any> {
    return this.axiosService.request("GET", `${this.baseUrl}/current-user`, null);
  }

  fetchReceiptsByCompany(companyId: number): Promise<any> {
    return this.axiosService.request("GET", `${this.baseUrl}/company/${companyId}`, null);
  }

  //TODO use axiosService
  uploadReceipt(formData: FormData): Observable<HttpEvent<string[]>> {
    const token = localStorage.getItem('token'); // Názov kľúča môže byť iný, záleží na tom, ako ho ukladáš
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<string[]>(`${this.baseUrl}/save`, formData, {
      headers: headers,
      reportProgress: true,
      observe: 'events'
    });
  }
}
