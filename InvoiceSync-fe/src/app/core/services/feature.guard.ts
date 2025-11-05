import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router
} from '@angular/router';
import {AuthService} from "../auth/auth.service";

@Injectable({
  providedIn: 'root'
})
export class FeatureGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const requiredFeature = route.data['feature'];
    const userFeatures = this.authService.getUserFeatures();

    if (userFeatures.includes(requiredFeature)) {
      return true;
    } else {
      this.router.navigate(['/home']);
      return false;
    }
  }
}
