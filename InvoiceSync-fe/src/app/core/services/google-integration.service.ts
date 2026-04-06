import {inject, Injectable} from '@angular/core';
import { environment } from '../../../environments/environment';
import { ApiPaths } from './api-paths';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

export interface GoogleStatusDTO {
  connected: boolean;
  email: string;
}

export interface GoogleUrlDTO {
  url: string;
}

@Injectable({
  providedIn: 'root'
})
export class GoogleIntegrationService {
  private readonly http = inject(HttpClient);

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.integrations.google.BASE;

  getStatus(): Observable<GoogleStatusDTO> {
    return this.http.get<GoogleStatusDTO>(`${this.baseUrl}${ApiPaths.integrations.google.STATUS}`);
  }

  getAuthUrl(): Observable<GoogleUrlDTO> {
    return this.http.get<GoogleUrlDTO>(`${this.baseUrl}${ApiPaths.integrations.google.URL}`)
  }

  disconnect(): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}${ApiPaths.integrations.google.DISCONNECT}`);
  }
}
