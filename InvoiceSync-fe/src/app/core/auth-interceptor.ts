import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Injectable} from "@angular/core";
import { AuthService } from "./auth/auth.service";
import {catchError, Observable, switchMap, throwError} from "rxjs";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();

    let authReq = req;
    if (token) {
      authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
    }

    return next.handle(authReq).pipe(
      catchError(err => {
        if (err.status === 401 && !req.url.includes('/auth/refresh-token')) {
          return this.authService.refreshToken().pipe(
            switchMap(() => {
              const newToken = this.authService.getToken();
              return next.handle(req.clone({
                headers: req.headers.set('Authorization', `Bearer ${newToken}`)
              }));
            }),
            catchError(() => {
              this.authService.logout();
              return throwError(err);
            })
          );
        }
        return throwError(err);
      })
    );
  }
}
