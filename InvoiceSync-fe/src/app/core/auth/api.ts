import { Injectable } from '@angular/core';
import axios, { AxiosInstance } from 'axios';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';
import {environment} from "../../../environments/environment";

@Injectable({ providedIn: 'root' })
export class ApiService {
  private api: AxiosInstance;
  private baseUrl: string = environment.apiUrl;

  constructor(private authService: AuthService) {
    this.api = axios.create({
      baseURL: this.baseUrl,
      withCredentials: true,
    });

    // Request interceptor
    this.api.interceptors.request.use(config => {
      const token = this.authService.getToken();
      if (token) {
        config.headers = config.headers || {};
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
    });

    // Response interceptor
    this.api.interceptors.response.use(
      response => response,
      async error => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          const newTokenResp = await firstValueFrom(this.authService.refreshToken());
          const newToken = newTokenResp.accessToken;
          this.authService.setToken(newToken);
          originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
          return this.api(originalRequest);
        }

        return Promise.reject(error);
      }
    );
  }

  get instance() {
    return this.api;
  }
}
