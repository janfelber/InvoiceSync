import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AxiosService} from "../axios.service";
import {ApiPaths} from "./api-paths";
import {CompanyRequest} from "../models/company-request";
import {ReceiptRequest} from "../models/receipt-request";
import {SplitRequest} from "../models/split-request";

@Injectable({
  providedIn: 'root'
})

export class ReceiptService {


  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.receipt.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }


  /**
   * Retrieves all companies associated with the currently logged-in user.
   *
   * Sends a GET request to: `/receipt/user?page={page}&size={size}`
   *
   * @param params - Pagination parameters.
   * @param params.page - The current page number (0-based).
   * @param params.size - The number of companies to retrieve per page.
   * @returns A Promise resolving to the server's response containing the list of companies.
   */
  findAllReceiptsByUser(params: { page: number; size: number }): Promise<any> {
    return this.axiosService.request(
      'GET', `${this.baseUrl}${ApiPaths.receipt.FIND_ALL_BY_USER}?page=${params.page}&size=${params.size}`, null
    );
  }

  getReceiptById(params: {receiptId: number}): Promise<any> {
    return this.axiosService.request(
      'GET', `${this.baseUrl}${ApiPaths.receipt.BY_ID(params.receiptId)}`, null
    )
  }

  findAllReceiptsByCompany(params: {page: number; size: number; companyId: number}): Promise<any> {
    return this.axiosService.request(
      'GET', `${this.baseUrl}${ApiPaths.receipt.FIND_BY_COMPANY(params.companyId)}?page=${params.page}&size=${params.size}`, null
    );
  }

  saveReceipt(params: { files: File[]; companyId: number }): Promise<any> {
    const formData = new FormData();
    for (const file of params.files) {
      formData.append('file', file, file.name);
    }
    formData.append('companyId', params.companyId.toString());

    return this.axiosService.request(
      'POST', `${this.baseUrl}${ApiPaths.receipt.SAVE}`, formData
    );
  }

  updateReceipt(params: { receiptId: number, receipt: ReceiptRequest }): Promise<any> {
    return this.axiosService.request(
      'PATCH', `${this.baseUrl}${ApiPaths.receipt.UPDATE_BY_ID(params.receiptId)}`, params.receipt
    );
  }

  exportReceiptPohoda(params: {receipt: ReceiptRequest}): Promise<any> {
    return this.axiosService.request(
        'POST', `${this.baseUrl}${ApiPaths.receipt.POHODA_RECEIPT_EXPORT}`, params.receipt,
        { responseType: 'blob' }
    )
  }

  splitItem(params: { itemId: number, request: SplitRequest }): Promise<any> {
    return this.axiosService.request(
      'POST', `${this.baseUrl}${ApiPaths.receipt.SPLIT_ITEM(params.itemId)}`, params.request,
      { responseType: 'blob' }
    )
  }


}
