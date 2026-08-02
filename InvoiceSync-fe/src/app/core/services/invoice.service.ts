import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AddDocumentData} from "../models/add-document-data";
import {Observable} from "rxjs";
import {HttpEvent} from "@angular/common/http";
import {ApiService} from "../auth/api";
import {MergeItemsRequest} from "../models/merge-items-request";

@Injectable({
  providedIn: 'root'
})

export class InvoiceService {


  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.invoice.BASE;

  constructor(
    private apiService: ApiService,
  ) {
  }

  /**
   * Retrieves all companies associated with the currently logged-in user.
   *
   * Sends a GET request to: `/invoice/user?page={page}&size={size}`
   *
   * @param params - Pagination parameters.
   * @param params.page - The current page number (0-based).
   * @param params.size - The number of companies to retrieve per page.
   * @returns A Promise resolving to the server's response containing the list of companies.
   */
  findAllInvoicesByUser(params: { page: number; size: number }): Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.invoice.FIND_ALL_BY_USER}?page=${params.page}&size=${params.size}`
    );
  }

  createInvoice(invoiceRequest: any, companyId: number) {
    return this.apiService.instance.post(
      `${this.baseUrl}${ApiPaths.invoice.CREATE_INVOICE}?companyId=${companyId}`,
      invoiceRequest
    );
  }

  findAllInvoicesByCompany(params: { page: number; size: number; companyId: number }): Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.invoice.FIND_BY_COMPANY(params.companyId)}?page=${params.page}&size=${params.size}`
    );
  }

  getInvoiceById(params: { invoiceId: number }): Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.invoice.BY_ID(params.invoiceId)}`,
    )
  }

  getInvoiceDocumentsById(params: { invoiceId: number }): Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.invoice.DOCUMENTS(params.invoiceId)}`
    )
  }

  uploadInvoice(params: { file: File; companyId: number; includeItems: boolean }): Promise<any> {
    const formData = new FormData();
    formData.append('file', params.file, params.file.name);

    return this.apiService.instance.post(
      `${this.baseUrl}${ApiPaths.invoice.EXTRACT(params.companyId)}?includeItems=${params.includeItems}`,
      formData
    );
  }

  addDocumentToInvoice(params: {
    invoiceId: number,
    file: File,
    additionalDocumentData: AddDocumentData
  }): Promise<any> {
    const formData = new FormData();
    formData.append('file', params.file);

    const blob = new Blob([JSON.stringify(params.additionalDocumentData)], {type: 'application/json'});
    formData.append('data', blob);

    return this.apiService.instance.post(
      `${this.baseUrl}${ApiPaths.invoice.ADD_DOCUMENT_TO_INVOICE(params.invoiceId)}`,
      formData,
    );
  }

  deleteDocumentFromInvoice(params: { documentId: number }): Promise<any> {
    return this.apiService.instance.delete(
      `${this.baseUrl}${ApiPaths.invoice.DELETE_DOCUMENT_FROM_INVOICE(params.documentId)}`
    )
  }

  generateInvoicePdf(invoiceId: number): Promise<Blob> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.invoice.GENERATE_PDF(invoiceId)}`,
      {responseType: 'blob'}
    );
  }

  mergeInvoiceItems(invoiceId: number, request: MergeItemsRequest): Promise<any> {
    return this.apiService.instance.post(
      `${this.baseUrl}${ApiPaths.invoice.MERGE_INVOICE_ITEMS(invoiceId)}`,
      request
    );
  }
}
