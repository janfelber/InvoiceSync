import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";
import {ChartAccountRequest} from "../models/ChartAccountRequest";

@Injectable({
  providedIn: 'root'
})

export class AccountChartService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.chartOfAccounts.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }


  /**
   * Get accounts for specific company
   * @param params Object with companyId property
   * @returns Promise resolving to a list of accounts
   */
  findAccountsByCompany(params: { companyId: number }) {
    const url = `${this.baseUrl}${ApiPaths.chartOfAccounts.FIND_BY_COMPANY(params.companyId)}`;
    return this.axiosService.request('GET', url, null);
  }

  saveChartAccount(chartAccount: ChartAccountRequest): Promise<any> {
    return this.axiosService.request(
      'POST',
      `${this.baseUrl}${ApiPaths.chartOfAccounts.SAVE}`,
      chartAccount
    );
  }

  importChartOfAccounts(params: { files: File[], companyId: number }) {
    const url = `${this.baseUrl}${ApiPaths.chartOfAccounts.IMPORT_ACCOUNTS}`;
    const formData = new FormData();
    for (const file of params.files) {
      formData.append('file', file, file.name);
      formData.append('companyId', params.companyId.toString());
    }

    return this.axiosService.request('POST', url, formData);
  }
}
