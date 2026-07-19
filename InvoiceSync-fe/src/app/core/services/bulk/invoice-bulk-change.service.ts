import {Injectable} from "@angular/core";
import {environment} from "../../../../environments/environment";
import {ApiService} from "../../auth/api";
import {ApiPaths} from "../api-paths";
import {Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";

export type InvoiceBulkAction = 'DELETE';

interface InvoiceBulkChangeRequest {
  ids: readonly number[];
}

@Injectable({ providedIn: 'root' })
export class InvoiceBulkChangeService {

  private baseUrl = environment.apiUrl.replace(/\/$/, '') + '/invoice/bulk';

  constructor(private http: HttpClient) {}

  executeBulkChange(action: InvoiceBulkAction, ids: readonly number[]): Observable<void> {
    const body: InvoiceBulkChangeRequest = { ids };
    const params = new HttpParams().set('action', action);

    return this.http.post<void>(`${this.baseUrl}/bulk-change/execute`,
      body,
      { params }
    );
  }

  bulkDownloadInvoices(invoiceIds: readonly number[]): Observable<Blob> {
    return this.http.post(
      `${this.baseUrl}${ApiPaths.invoice.BULK_DOWNLOAD}`,
      invoiceIds,
      {
        responseType: 'blob'
      }
    );
  }
}
