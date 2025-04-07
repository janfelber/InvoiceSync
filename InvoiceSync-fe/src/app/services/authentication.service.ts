import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {RegisterRequest} from "../models/register-request";
import {AuthenticationReponse} from "../models/authentication-reponse";
import {VerificationRequest} from "../models/verification-request";
import {AuthenticationRequest} from "../models/authentication-request";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class  AuthenticationService {

  private baseUrl = environment.apiUrl + '/v1/auth';

  constructor(
    private http: HttpClient
  ) { }

  register(
    registerRequest : RegisterRequest
  ) {
    return this.http.post<AuthenticationReponse>
    (`${this.baseUrl}/register`, registerRequest);
  }

  login(
    authRequest: AuthenticationRequest
  ) {
    return this.http.post<AuthenticationReponse>
    (`${this.baseUrl}/authenticate`, authRequest);
  }

  verifyCode(verificationRequest: VerificationRequest) {
    return this.http.post<AuthenticationReponse>
    (`${this.baseUrl}/verify`, verificationRequest);
  }


}
