import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { ApiPaths } from './api-paths';
import { ApiService } from '../auth/api';

@Injectable({
  providedIn: 'root'
})
export class GoogleIntegrationService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.integrations.google.BASE;

  constructor(private apiService: ApiService) {}

  getStatus(): Promise<{ connected: boolean; email: string }> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.integrations.google.STATUS}`)
      .then(res => res.data);
  }

  getAuthUrl(): Promise<{ url: string }> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.integrations.google.URL}`)
      .then(res => res.data);
  }

  disconnect(): Promise<any> {
    return this.apiService.instance.delete(`${this.baseUrl}${ApiPaths.integrations.google.DISCONNECT}`)
      .then(res => res.data);
  }
}
