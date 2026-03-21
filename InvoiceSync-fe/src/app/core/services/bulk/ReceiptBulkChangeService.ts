import { Injectable } from '@angular/core';
import { ApiService } from '../../auth/api';
import { environment } from '../../../../environments/environment';

export type ReceiptBulkAction = 'DELETE';

@Injectable({ providedIn: 'root' })
export class ReceiptBulkChangeService {

  private baseUrl = environment.apiUrl.replace(/\/$/, '') + '/receipt/bulk';

  constructor(private apiService: ApiService) {}

  executeBulkChange(action: ReceiptBulkAction, ids: number[]): Promise<void> {
    return this.apiService.instance.post(
      `${this.baseUrl}/bulk-change/execute?action=${action}`,
      { ids }
    );
  }
}
