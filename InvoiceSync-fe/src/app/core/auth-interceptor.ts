import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth/auth.service';
import { SKIP_AUTH } from './auth/skip-auth.token';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  if (req.context.get(SKIP_AUTH)) {
    return next(req);
  }

  const token = authService.getToken();
  const authReq = token
    ? req.clone({ headers: req.headers.set('Authorization', `Bearer ${token}`) })
    : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status !== 401) {
        return throwError(() => err);
      }

      return authService.refreshToken().pipe(
        switchMap(() => {
          const newToken = authService.getToken();
          return next(req.clone({
            headers: req.headers.set('Authorization', `Bearer ${newToken}`)
          }));
        }),
        catchError(() => {
          authService.logout();
          return throwError(() => err);
        })
      );
    })
  );
};
