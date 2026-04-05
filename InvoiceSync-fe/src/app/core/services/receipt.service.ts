import {inject, Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ApiPaths} from "./api-paths";
import {ApiService} from "../auth/api";
import {AddDocumentData} from "../models/add-document-data";
import {MergeItemsRequest} from "../models/merge-items-request";
import {Details} from "../../pages/receipts/details";
import {Partner} from "../models/partner";
import {Company} from "../models/company";
import {Observable} from "rxjs";
import {ReceiptRequest} from "../models/receipt-request";

export interface ReceiptItemDTO {
  id: number;
  name: string;
  quantity: number;
  vatRate: number;
  unitPriceWithoutVat: number;
  unitPriceWithVat: number;
  totalItemPriceWithVat: number;
  accountValue: string;
  accountText: string;
}

export interface ReceiptDTO {
  id: number;
  orderNumber: number;
  receiptNumber: string;
  paymentType: string;
  createdAt: string;
  receiptDetails: Details;
  partner: Partner;
  items: ReceiptItemDTO[];
  company: Company;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({
  providedIn: 'root'
})

export class ReceiptService {

  private readonly http = inject(HttpClient);

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.receipt.BASE;

  constructor(
    private apiService: ApiService
  ) { }


  /**
   * Retrieves all companies associated with the currently logged-in user.
   *
   * Sends a GET request to: `/receipt/user?page={page}&size={size}`
   *
   * @param page - The current page number (0-based).
   * @param size - The number of companies to retrieve per page.
   * @returns A Promise resolving to the server's response containing the list of companies.
   */
  findAllReceiptsByUser(page = 0, size = 50): Observable<PageResponse<ReceiptDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<ReceiptDTO>>(`${this.baseUrl}${ApiPaths.receipt.FIND_ALL_BY_USER}`, { params });
  }

  /** @deprecated Use findAllReceiptsByUser() instead */
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
  findAllReceiptsByUserLegacy(params: { page: number; size: number }): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.receipt.FIND_ALL_BY_USER}?page=${params.page}&size=${params.size}`,
    );
  }

  getReceiptById(params: {receiptId: number}): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.receipt.BY_ID(params.receiptId)}`
    )
  }

  findAllReceiptsByCompany(page = 0, size = 50, companyId: number): Observable<PageResponse<ReceiptDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<ReceiptDTO>>(`${this.baseUrl}${ApiPaths.receipt.FIND_BY_COMPANY(companyId)}`, { params });
  }

  /** @deprecated Use findAllReceiptsByCompany() instead */
  findAllReceiptsByCompanyLegacy(params: {page: number; size: number; companyId: number}): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.receipt.FIND_BY_COMPANY(params.companyId)}?page=${params.page}&size=${params.size}`
    );
  }


  save(files: File[], companyId: number): Observable<void> {
    const formData = new FormData();
    files.forEach(file => formData.append('file', file, file.name));
    formData.append('companyId', companyId.toString());
    return this.http.post<void>(`${this.baseUrl}${ApiPaths.receipt.SAVE}`, formData);
  }

  /** @deprecated Use save() instead */
  saveReceiptLegacy(params: { files: File[]; companyId: number }): Promise<any> {
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

  exportReceiptPohoda(params: {receipt: ReceiptRequest}): Promise<any> {
    return this.apiService.instance.post(`${this.baseUrl}${ApiPaths.receipt.POHODA_RECEIPT_EXPORT}`, params.receipt,
    )
  }

  deleteReceipt(receiptId:number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}${ApiPaths.receipt.DELETE(receiptId)}`);
  }

  /** @deprecated Use deleteReceipt() instead */
  deleteReceiptLegacy(receiptId: number): Promise<any> {
    return this.apiService.instance.delete(`${this.baseUrl}${ApiPaths.receipt.DELETE(receiptId)}`
    )
  }

  mergeReceiptItems(receiptId: number, itemToMerge: MergeItemsRequest): Promise<any> {
    return this.apiService.instance.post(`${this.baseUrl}${ApiPaths.receipt.MERGE_RECEIPT_ITEMS(receiptId)}`, itemToMerge)
  }

}
