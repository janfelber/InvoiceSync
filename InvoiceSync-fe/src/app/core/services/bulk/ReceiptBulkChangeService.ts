import { Injectable } from '@angular/core';
import { ApiService } from '../../auth/api';
import { environment } from '../../../../environments/environment';

export type ReceiptBulkAction = 'DELETE' | 'COMPANY_REASSIGN';

@Injectable({ providedIn: 'root' })
export class ReceiptBulkChangeService {

  private baseUrl = environment.apiUrl.replace(/\/$/, '') + '/receipt/bulk';

  constructor(private apiService: ApiService) {}

  executeBulkChange(action: ReceiptBulkAction, ids: number[], companyId?: number): Promise<void> {
    const body: any = { ids };
    if (companyId !== undefined) body.companyId = companyId;
    return this.apiService.instance.post(
      `${this.baseUrl}/bulk-change/execute?action=${action}`,
      body
    );
  }
}
