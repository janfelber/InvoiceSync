import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import {Observable, of, tap} from 'rxjs';
import {catchError, finalize, map, shareReplay} from "rxjs/operators";
import {Router} from "@angular/router";
import {environment} from "../../../environments/environment";
import {SKIP_AUTH} from "./skip-auth.token";

interface User {
  role: string;
  features: string[];
}

interface LoginResponse {
  accessToken: string;
  user: User;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private accessToken: string | null = null;
  currentUser: User | null = null;
  userRole: string | null = null;
  userFeatures: string[] = [];
  private baseUrl: string = environment.apiUrl;
  private refreshInProgress$: Observable<LoginResponse> | null = null;
  private authChannel = new BroadcastChannel('auth');

  constructor(private http: HttpClient, private router: Router) {
    this.authChannel.onmessage = (event) => {
      if (event.data === 'logout') {
        this.clearLocalState();
        this.router.navigate(['/login']);
      }
    };
  }

  login(credentials: { username: string; password: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.baseUrl}/auth/login`,
      credentials,
      { withCredentials: true, context: new HttpContext().set(SKIP_AUTH, true) }
    ).pipe(
      tap(res => {
        this.accessToken = res.accessToken;
        this.currentUser = res.user;
        this.userRole = res.user.role;
        this.userFeatures = res.user.features
      })
    );
  }

  register(data: { fullName: string; username: string; registrationNumber: string; password: string; email: string; phoneNumber: string }): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/auth/register`,
      data,
      { context: new HttpContext().set(SKIP_AUTH, true) }
    ).pipe(
      tap(() => {
        this.router.navigate(['/login']);
      })
    );
  }

  refreshToken(): Observable<LoginResponse> {
    // Refresh tokens are single-use (rotated on every call) — if multiple requests
    // hit 401 at the same time, they must share one in-flight refresh instead of
    // each firing its own, or all but the first will be rejected as token reuse.
    if (this.refreshInProgress$) {
      return this.refreshInProgress$;
    }

    this.refreshInProgress$ = this.http.post<LoginResponse>(
      `${this.baseUrl}/auth/refresh-token`,
      {},
      { withCredentials: true, context: new HttpContext().set(SKIP_AUTH, true) }
    ).pipe(
      tap(res => {
        this.accessToken = res.accessToken;
        this.currentUser = res.user;
        this.userRole = res.user.role;
        this.userFeatures = res.user.features;
      }),
      shareReplay(1),
      finalize(() => this.refreshInProgress$ = null)
    );

    return this.refreshInProgress$;
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

  getUserFeatures(): string[] {
    return this.userFeatures;
  }

  logout() {
    this.http.post(`${this.baseUrl}/auth/logout`, {}, { withCredentials: true }).pipe(
      catchError(() => of(null)),
      finalize(() => {
        this.clearLocalState();
        this.authChannel.postMessage('logout');
        this.router.navigate(['/login']);
      })
    ).subscribe();
  }

  private clearLocalState(): void {
    this.accessToken = null;
    this.currentUser = null;
    this.userRole = null;
    this.userFeatures = [];
  }
}
