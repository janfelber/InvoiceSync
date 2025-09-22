import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";
import {AddDocumentData} from "../models/add-document-data";

@Injectable({
  providedIn: 'root'
})

export class InvoiceService {


  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.invoice.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }

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
    return this.axiosService.request(
      'GET', `${this.baseUrl}${ApiPaths.invoice.FIND_ALL_BY_USER}?page=${params.page}&size=${params.size}`, null
    );
  }

  findAllInvoicesByCompany(params: { page: number; size: number; companyId: number}): Promise<any> {
    return this.axiosService.request(
      'GET', `${this.baseUrl}${ApiPaths.invoice.FIND_BY_COMPANY(params.companyId)}?page=${params.page}&size=${params.size}`, null
    );
  }

  getInvoiceById(params: { invoiceId: number }): Promise<any> {
    return this.axiosService.request(
      'GET', `${this.baseUrl}${ApiPaths.invoice.BY_ID(params.invoiceId)}`, null
    )
  }

  getInvoiceDocumentsById(params: { invoiceId:number }): Promise<any> {
    return this.axiosService.request(
      'GET', `${this.baseUrl}${ApiPaths.invoice.DOCUMENTS(params.invoiceId)}`, null
    )
  }

  addDocumentToInvoice(params: { invoiceId: number, file: File, additionalDocumentData: AddDocumentData }): Promise<any> {
    const formData = new FormData();
    formData.append('file', params.file);

    const blob = new Blob([JSON.stringify(params.additionalDocumentData)], { type: 'application/json' });
    formData.append('data', blob);

    return this.axiosService.request(
      'POST',
      `${this.baseUrl}${ApiPaths.invoice.ADD_DOCUMENT_TO_INVOICE(params.invoiceId)}`,
      formData,
    );
  }
}
