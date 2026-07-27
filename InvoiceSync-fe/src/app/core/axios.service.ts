import { Injectable } from '@angular/core';
import axios from 'axios';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AxiosService {

  constructor() {
    axios.defaults.baseURL = environment.apiUrl;

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
