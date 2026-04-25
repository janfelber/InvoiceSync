import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AxiosService} from "../axios.service";
import {ApiPaths} from "./api-paths";
import {CompanyRequest} from "../models/company-request";
import {ReceiptRequest} from "../models/receipt-request";
import {ApiService} from "../auth/api";
import {AddDocumentData} from "../models/add-document-data";
import {MergeItemsRequest} from "../models/merge-items-request";

@Injectable({
  providedIn: 'root'
})

export class ReceiptService {


  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.receipt.BASE;

  constructor(
    private apiService: ApiService
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
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.receipt.FIND_ALL_BY_USER}?page=${params.page}&size=${params.size}`,
    );
  }

  getReceiptById(params: {receiptId: number}): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.receipt.BY_ID(params.receiptId)}`
    )
  }

  findAllReceiptsByCompany(params: {page: number; size: number; companyId: number}): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.receipt.FIND_BY_COMPANY(params.companyId)}?page=${params.page}&size=${params.size}`
    );
  }

  saveReceipt(params: { files: File[]; companyId: number }): Promise<any> {
    const formData = new FormData();
    for (const file of params.files) {
      formData.append('file', file, file.name);
    }
    formData.append('companyId', params.companyId.toString());

    return this.apiService.instance.post(`${this.baseUrl}${ApiPaths.receipt.SAVE}`, formData
    );
  }

  getReceiptDocumentsById(params: { receiptId: number }): Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.receipt.DOCUMENTS(params.receiptId)}`
    )
  }

  addDocumentToReceipt(params: {
    receiptId: number,
    file: File,
    additionalDocumentData: AddDocumentData
  }): Promise<any> {
    const formData = new FormData();
    formData.append('file', params.file);

    const blob = new Blob([JSON.stringify(params.additionalDocumentData)], {type: 'application/json'});
    formData.append('data', blob);

    return this.apiService.instance.post(
      `${this.baseUrl}${ApiPaths.receipt.ADD_DOCUMENT_TO_RECEIPT(params.receiptId)}`,
      formData,
    )
  }

  deleteDocumentFromReceipt(params: { documentId: number }): Promise<any> {
    return this.apiService.instance.delete(
      `${this.baseUrl}${ApiPaths.receipt.DELETE_DOCUMENT_FROM_RECEIPT(params.documentId)}`
    )
  }

  updateReceipt(params: { receiptId: number, receipt: ReceiptRequest }): Promise<any> {
    return this.apiService.instance.patch(
      `${this.baseUrl}${ApiPaths.receipt.UPDATE_BY_ID(params.receiptId)}`, params.receipt
    );
  }

  exportReceiptPohoda(receiptId: number): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.receipt.POHODA_RECEIPT_EXPORT(receiptId)}`, {
      responseType: 'blob'
    })
  }

  deleteReceipt(receiptId: number): Promise<any> {
    return this.apiService.instance.delete(`${this.baseUrl}${ApiPaths.receipt.DELETE(receiptId)}`
    )
  }

  mergeReceiptItems(receiptId: number, itemToMerge: MergeItemsRequest): Promise<any> {
    return this.apiService.instance.post(`${this.baseUrl}${ApiPaths.receipt.MERGE_RECEIPT_ITEMS(receiptId)}`, itemToMerge)
  }

}
