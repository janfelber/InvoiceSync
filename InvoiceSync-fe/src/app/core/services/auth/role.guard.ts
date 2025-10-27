import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import {AuthService} from "../../auth/auth.service";

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {

  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    const expectedRoles = route.data['roles'] as string[];

    // ak už máme rolu v pamäti, netreba čakať
    const existingRole = this.auth.getUserRole();
    if (existingRole) {
      return of(expectedRoles.includes(existingRole));
    }

    // inak počkajme na refresh token
    return this.auth.refreshToken().pipe(
      map(res => {
        const role = this.auth.getUserRole();
        if (role && expectedRoles.includes(role)) {
          return true;
        }
        this.router.navigate(['/home']);
        return false;
      }),
      catchError(() => {
        this.router.navigate(['/home']);
        return of(false);
      })
    );
  }
}
