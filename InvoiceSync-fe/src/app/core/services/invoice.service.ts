import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AddDocumentData} from "../models/add-document-data";
import {Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {PageResponse} from "../models/page-response";
import {InvoiceResponseTable} from "../../pages/invoices/invoice-response-table";
import {InvoiceDetailResponse} from "../../pages/invoices/invoice-detail-response";
import {InvoiceRequestDto} from "../models/invoice-request-dto";
import {InvoiceDocumentResponse} from "../models/invoice-document-response";

@Injectable({
  providedIn: 'root'
})

export class InvoiceService {


  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.invoice.BASE;

  constructor(private http: HttpClient) {}

  /**
   * Retrieves all invoices associated with the currently logged-in user.
   *
   * Sends a GET request to: `/invoice/user?page={page}&size={size}`
   *
   * @param params - Pagination parameters.
   * @param params.page - The current page number (0-based).
   * @param params.size - The number of invoices to retrieve per page.
   * @returns An Observable containing the paginated list of invoices.
   */
  findAllInvoicesByUser(params: { page: number; size: number }): Observable<PageResponse<InvoiceResponseTable>> {
    const httpParams = new HttpParams()
      .set('page', params.page)
      .set('size', params.size);

    return this.http.get<PageResponse<InvoiceResponseTable>>(
      `${this.baseUrl}${ApiPaths.invoice.FIND_ALL_BY_USER}`,
      {params: httpParams}
    );
  }

  createInvoice(invoiceRequest: InvoiceRequestDto, companyId: number): Observable<number> {
    const params = new HttpParams().set('companyId', companyId);

    return this.http.post<number>(
      `${this.baseUrl}${ApiPaths.invoice.CREATE_INVOICE}`,
      invoiceRequest,
      {params}
    );
  }


  findAllInvoicesByCompany(params: { page: number; size: number; companyId: number }): Observable<PageResponse<InvoiceResponseTable>> {
    const httpParams = new HttpParams()
      .set('page', params.page)
      .set('size', params.size);

    return this.http.get<PageResponse<InvoiceResponseTable>>(
      `${this.baseUrl}${ApiPaths.invoice.FIND_BY_COMPANY(params.companyId)}`,
      {params: httpParams}
    );
  }

  getInvoiceById(invoiceId: number): Observable<InvoiceDetailResponse> {
    return this.http.get<InvoiceDetailResponse>(
      `${this.baseUrl}${ApiPaths.invoice.BY_ID(invoiceId)}`
    );
  }

  getInvoiceDocumentsById(invoiceId: number): Observable<InvoiceDocumentResponse[]> {
    return this.http.get<InvoiceDocumentResponse[]>(
      `${this.baseUrl}${ApiPaths.invoice.DOCUMENTS(invoiceId)}`
    );
  }


  uploadInvoice(params: { file: File; companyId: number }): Observable<number> {
    const formData = new FormData();
    formData.append('file', params.file, params.file.name);
    formData.append('companyId', params.companyId.toString());

    return this.http.post<number>(
      `${this.baseUrl}${ApiPaths.invoice.SAVE}`,
      formData
    );
  }

  addDocumentToInvoice(params: {
    invoiceId: number,
    file: File,
    additionalDocumentData: AddDocumentData
  }): Observable<void> {
    const formData = new FormData();
    formData.append('file', params.file, params.file.name);

    const blob = new Blob([JSON.stringify(params.additionalDocumentData)], {type: 'application/json'});
    formData.append('data', blob);

    return this.http.post<void>(
      `${this.baseUrl}${ApiPaths.invoice.ADD_DOCUMENT_TO_INVOICE(params.invoiceId)}`,
      formData
    );
  }

  deleteDocumentFromInvoice(documentId: number): Observable<number> {
    return this.http.delete<number>(
      `${this.baseUrl}${ApiPaths.invoice.DELETE_DOCUMENT_FROM_INVOICE(documentId)}`
    );
  }

  generateInvoicePdf(invoiceId: number): Observable<Blob> {
    return this.http.get(
      `${this.baseUrl}${ApiPaths.invoice.GENERATE_PDF(invoiceId)}`,
      {responseType: 'blob'}
    );
  }

  // TODO Fix merge items bug: the backend Invoice API path does not exist.
  // mergeInvoiceItems(invoiceId: number, request: MergeItemsRequest): Observable<void> {
  //   return this.http.post<void>(
  //     `${this.baseUrl}${ApiPaths.invoice.MERGE_ITEMS(invoiceId)}`,
  //     request
  //   );
  // }
}
