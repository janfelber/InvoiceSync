import { Injectable } from '@angular/core';
// @ts-ignore
import Keycloak from "keycloak-js";
import {UserProfile} from "./user-profile";

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak: Keycloak | undefined
  private _profile: UserProfile | undefined

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak(
        {
          url: 'http://localhost:9090',
          realm: 'invoice_sync',
          clientId: 'is'
        }
      )
    }
    return this._keycloak
  }

  getUserRoles(): string[] {
    return this.keycloak?.realmAccess?.roles || [];
  }

  isSuperAdmin(): boolean {
    return this.getUserRoles().includes('superadmin');
  }

  getUserProfile(): UserProfile | undefined {
    return this._profile
  }

  constructor() { }

  async init () {
    console.log('Autheication the user....')
    const autheticated = await this.keycloak?.init({
      onLoad: 'login-required'
    })

    if (autheticated) {
      console.log('User is authecicated')
      this._profile = (await this.keycloak?.loadUserProfile()) as UserProfile;
      this._profile.token = this.keycloak?.token;

      console.log(this._profile.token)
    }

  }

  login() {
    return this.keycloak?.login();
  }

  logout() {
    return this.keycloak?.logout()
  }

}


