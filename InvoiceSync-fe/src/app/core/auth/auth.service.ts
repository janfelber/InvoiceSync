import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, of, tap} from 'rxjs';
import {catchError, map} from "rxjs/operators";
import {Router} from "@angular/router";
import {environment} from "../../../environments/environment";

interface SidenavItem {
  label: string;
  icon: string;
  route: string;
  children?: SidenavItem[];
}

interface User {
  sidenav: SidenavItem[];
  role: string;
}

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private accessToken: string | null = null;
  currentUser: User | null = null;
  userRole: string | null = null;
  private baseUrl: string = environment.apiUrl;

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: { username: string; password: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.baseUrl}/auth/login`,
      credentials,
      { withCredentials: true }
    ).pipe(
      tap(res => {
        this.accessToken = res.accessToken;
        this.currentUser = res.user;
        this.userRole = res.user.role;
        console.log(this.userRole);
      })
    );
  }

  register(data: { fullName: string; username: string; password: string; email: string; phoneNumber: string }): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/auth/register`,
      data
    ).pipe(
      tap(() => {
        this.router.navigate(['/login']);
      })
    );
  }

  refreshToken(): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.baseUrl}/auth/refresh-token`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(res => {
        this.accessToken = res.accessToken;
        this.currentUser = res.user;
        this.userRole = res.user.role
      })
    );
  }

  // alls when the application starts or reloads
  init(): Observable<boolean> {
    return this.refreshToken().pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  getToken(): string | null {
    return this.accessToken;
  }

  setToken(token: string) {
    this.accessToken = token;
  }

  getUserRole(): string | null {
    if (!this.userRole && this.currentUser) {
      this.userRole = this.currentUser.role;
    }
    return this.userRole || null;
  }

  logout() {
    this.http.post(`${this.baseUrl}/auth/logout`, {}, { withCredentials: true }).subscribe(() => {
      this.accessToken = null;
      this.currentUser = null;
      this.router.navigate(['/login']);
    });
  }
}
