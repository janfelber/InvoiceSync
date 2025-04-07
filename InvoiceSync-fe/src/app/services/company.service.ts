import {environment} from "../../environments/environment";
import {Injectable} from "@angular/core";
import {AxiosService} from "../axios.service";

@Injectable({
  providedIn: 'root'
})

export class CompanyService {

  private baseUrl = environment.apiUrl + "/v1/company";

  constructor(
    private axiosService: AxiosService
  ) { }

  fetchAllCompanies(): Promise<any> {
    return this.axiosService.request("GET", `${this.baseUrl}/user`, null);
  }

  addCompany(companyInfo: FormData): Promise<any> {
    return this.axiosService.request("POST", `${this.baseUrl}/add`, companyInfo);
  }
}


