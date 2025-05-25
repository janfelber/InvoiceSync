import { Injectable } from '@angular/core';
import axios from 'axios';
import { environment } from '../environments/environment';
import {jwtDecode} from 'jwt-decode';
import {KeycloakService} from "../keycloak/keycloak.service";

@Injectable({
  providedIn: 'root'
})
export class AxiosService {

  constructor(private keycloakService: KeycloakService) {
    axios.defaults.baseURL = environment.apiUrl;
  }

  getAuthToken(): string | null {
    const token = this.keycloakService.keycloak.token;
    return token && token.trim() !== '' ? token : null;
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
