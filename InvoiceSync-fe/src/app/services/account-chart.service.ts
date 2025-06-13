import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";

@Injectable({
  providedIn: 'root'
})

export class AccountChartService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.chartOfAccounts.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }

  finalAll() {
    const url = `${this.baseUrl}${ApiPaths.chartOfAccounts.ALL}`;
    return this.axiosService.request('GET', url, null);
  }
}
