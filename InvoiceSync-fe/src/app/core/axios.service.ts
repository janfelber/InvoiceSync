import { Injectable } from '@angular/core';
import axios from 'axios';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AxiosService {

  private accessToken: string | null = null;

  constructor() {
    axios.defaults.baseURL = environment.apiUrl;

  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  setAuthToken(token: string | null ): void {
    if (token != null) {
      window.localStorage.setItem("auth_token", token)
    }
    else {
      window.localStorage.removeItem("auth_token")
    }
  }

  request(method: string, url: string, data: any, config: any = {}): Promise<any> {
    let headers = {};


    return axios({
      method: method,
      url: url,
      data: data,
      headers: headers,
      ...config
    })
  }
}
