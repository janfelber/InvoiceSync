import { Injectable } from '@angular/core';
import axios from 'axios';
import { environment } from '../environments/environment';
import {jwtDecode} from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AxiosService {

  constructor() {
    axios.defaults.baseURL = environment.apiUrl;
    axios.defaults.headers.post["Content-Type"] = "application/json"
  }

  getAuthToken(): string | null {
    return window.localStorage.getItem("token");
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

    if (this.getAuthToken() != null) {
      headers = {"Authorization": "Bearer " + this.getAuthToken()}
    }

    return axios({
      method: method,
      url: url,
      data: data,
      headers: headers,
      ...config
    })
  }
}
