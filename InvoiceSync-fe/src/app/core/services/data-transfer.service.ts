import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";
import {HttpHeaders} from "@angular/common/http";
import { ApiService } from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class DataTransferService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.data_transfer.BASE;

  constructor(
    private apiService: ApiService
  ) { }

  findAllConvertsByUser(params?: { page?: number; size?: number }): Promise<any> {
    const queryParams = [];
    if (params?.page !== undefined) queryParams.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParams.push(`size=${params.size}`);
    const queryString = queryParams.length > 0 ? `?${queryParams.join('&')}` : '';

    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.data_transfer.FIND_ALL_BY_USER}${queryString}`,
    )
  }

  getConvertById(params: {convertId: number}): Promise<any> {
    return this.apiService.instance.get(
`${this.baseUrl}${ApiPaths.data_transfer.BY_ID(params.convertId)}`
    )
  }


  saveConvert(params: { files: File[] }): Promise<any> {
    const formData = new FormData();
    for (const file of params.files) {
      formData.append('file', file, file.name);
    }

    return this.apiService.instance.post(
      `${this.baseUrl}${ApiPaths.data_transfer.UPLOAD}`,
      formData
    );
  }

  saveMapping(convertId: number, mappings: any[]): Promise<any> {
    return this.apiService.instance.post(
      `${this.baseUrl}${ApiPaths.data_transfer.UPDATE_MAPPING(convertId)}`,
      mappings
    )
  }

  downloadConvert(convertId:number): Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.data_transfer.DOWNLOAD(convertId)}`
    )
  }

}
