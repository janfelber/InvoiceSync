import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AxiosService} from "../axios.service";

@Injectable({
  providedIn: 'root'
})

export class CompanyService {

  private baseUrl = environment.apiUrl + '/api/v1/company';

  constructor(
    private axiosService: AxiosService
  ) { }

  updateCompany(id: number, company: any): Promise<any> {
    return this.axiosService.request(
      'POST',
      `${this.baseUrl}/update/${id}`,
      company
    );
  }

  deleteCompany(id: number): Promise<any> {
    return this.axiosService.request(
      'DELETE',
      `${this.baseUrl}/delete/${id}`,
      null
    )
  }
}
