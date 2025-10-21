import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {PostingAccountRequest} from "../models/PostingAccountRequest";
import {ApiService} from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class PostingAccountService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.postingAccounts.BASE;

  constructor(
    private apiService: ApiService
  ) {
  }


  /**
   * Get accounts for specific company
   * @param params Object with companyId property
   * @returns Promise resolving to a list of accounts
   */
  findAccountsByCompany(params: { page: number; size: number; type: string; companyId: number }) {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.postingAccounts.FIND_BY_COMPANY(params.companyId)}?page=${params.page}&size=${params.size}&type=${params.type}`,
    );
  }

  findAvailableAccounts(params: { companyId: number, type: string }) {
    const url = `${this.baseUrl}${ApiPaths.postingAccounts.AVAILABLE_ACCOUNTS(params.companyId)}?type=${params.type}`;
    return this.apiService.instance.get(url);
  }

  deleteAccount(params: { accountId: number }) {
    return this.apiService.instance.delete(
      `${this.baseUrl}${ApiPaths.postingAccounts.DELETE(params.accountId)}`,
    )
  }

  savePostingAccount(postingAccount: PostingAccountRequest): Promise<any> {
    return this.apiService.instance.post(
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

    return this.apiService.instance.post(url, formData);
  }
}
