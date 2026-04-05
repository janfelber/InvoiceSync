import {inject, Injectable} from '@angular/core';
import { ApiService } from '../../auth/api';
import { environment } from '../../../../environments/environment';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";

export type ReceiptBulkAction = 'DELETE' | 'COMPANY_REASSIGN';

@Injectable({ providedIn: 'root' })
export class ReceiptBulkChangeService {

  private readonly http = inject(HttpClient);

  private baseUrl = environment.apiUrl.replace(/\/$/, '') + '/receipt/bulk';

  constructor(private apiService: ApiService) {}

  executeBulkChange(action: ReceiptBulkAction, ids: number[], companyId?: number): Observable<void> {
    const body: any = { ids };
    if (companyId !== undefined) body.companyId = companyId;
    return this.http.post<void>(`${this.baseUrl}/bulk-change/execute`, body, {
      params: new HttpParams().set('action', action)
    });
  }

  /** @deprecated Use executeBulkChange() instead */
  executeBulkChangeLegacy(action: ReceiptBulkAction, ids: number[], companyId?: number): Promise<void> {
    const body: any = { ids };
    if (companyId !== undefined) body.companyId = companyId;
    return this.apiService.instance.post(
      `${this.baseUrl}/bulk-change/execute?action=${action}`,
      body
    );
  }
}
