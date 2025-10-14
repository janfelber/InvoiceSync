import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";
import {PostingAccountRequest} from "../models/PostingAccountRequest";

@Injectable({
  providedIn: 'root'
})

export class PostingAccountService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.postingAccounts.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }


  /**
   * Get accounts for specific company
   * @param params Object with companyId property
   * @returns Promise resolving to a list of accounts
   */
  findAccountsByCompany(params: { page: number; size: number; type: string; companyId: number }) {
    return this.axiosService.request(
      'GET',
      `${this.baseUrl}${ApiPaths.postingAccounts.FIND_BY_COMPANY(params.companyId)}?page=${params.page}&size=${params.size}&type=${params.type}`,
      null
    );
  }

  findAvailableAccounts(params: { companyId: number, type: string }) {
    const url = `${this.baseUrl}${ApiPaths.postingAccounts.AVAILABLE_ACCOUNTS(params.companyId)}?type=${params.type}`;
    return this.axiosService.request('GET', url, null);
  }

  deleteAccount(params: { accountId: number }) {
    return this.axiosService.request(
      'DELETE',
      `${this.baseUrl}${ApiPaths.postingAccounts.DELETE(params.accountId)}`,
      null
    )
  }

  savePostingAccount(postingAccount: PostingAccountRequest): Promise<any> {
    return this.axiosService.request(
      'POST',
      `${this.baseUrl}${ApiPaths.postingAccounts.SAVE}`,
      postingAccount
    );
  }

  importChartOfAccounts(params: { files: File[], companyId: number }) {
    const url = `${this.baseUrl}${ApiPaths.postingAccounts.IMPORT_ACCOUNTS}`;
    const formData = new FormData();
    for (const file of params.files) {
      formData.append('file', file, file.name);
      formData.append('companyId', params.companyId.toString());
    }

    return this.axiosService.request('POST', url, formData);
  }
}
