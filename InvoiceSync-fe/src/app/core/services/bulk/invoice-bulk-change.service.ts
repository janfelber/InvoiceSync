import {Injectable} from "@angular/core";
import {environment} from "../../../../environments/environment";
import {ApiService} from "../../auth/api";
import {ApiPaths} from "../api-paths";

export type InvoiceBulkAction = 'DELETE';

@Injectable({ providedIn: 'root' })
export class InvoiceBulkChangeService {

  private baseUrl = environment.apiUrl.replace(/\/$/, '') + '/invoice/bulk';

  constructor(private apiService: ApiService) {}

  executeBulkChange(action: InvoiceBulkAction, ids: number[]): Promise<void> {
    const body: any = { ids };
    return this.apiService.instance.post(
      `${this.baseUrl}/bulk-change/execute?action=${action}`,
      body
    );
  }

  bulkDownloadInvoices(invoiceIds: Set<number>): Promise<any> {
    return this.apiService.instance.post(
      `${this.baseUrl}${ApiPaths.invoice.BULK_DOWNLOAD}`,
      Array.from(invoiceIds),
      { responseType: 'blob' }
    );
  }
}
