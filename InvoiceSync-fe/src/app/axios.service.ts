import { Injectable } from '@angular/core';
import axios from 'axios';
import {jwtDecode} from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AxiosService {

  constructor() {
    axios.defaults.baseURL = "http://localhost:8080"
    axios.defaults.headers.post["Content-Type"] = "application/json"
  }

  getUserId(): string | null {
    const token = this.getAuthToken();
    if (token) {
      try {
        const token =  this.getAuthToken() as string;
        const decodedToken: any = jwtDecode(token) as string;
        return decodedToken.userId || null;
      } catch (error) {
        console.error("Invalid token", error);
        return null;
      }
    }
    return null;
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
      data:data,
      headers:headers,
      ...config
    })
  }
}
