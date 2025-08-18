import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { inject } from '@angular/core';
import { KeycloakService } from '../../keycloak/keycloak.service';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const keycloakService = inject(KeycloakService);
  const router = inject(Router);

  if (!keycloakService.keycloak?.authenticated) {
    keycloakService.login();
    return false;
  }

  if (keycloakService.keycloak?.isTokenExpired()) {
    keycloakService.keycloak.updateToken(30).catch(() => {
      keycloakService.login();
    });
    return false;
  }

  // Kontrola rolí definovaných v route
  const allowedRoles = route.data['roles'] as string[] | undefined;
  if (allowedRoles && allowedRoles.length > 0) {
    const userRoles = keycloakService.getUserRoles();
    const hasRole = allowedRoles.some(role => userRoles.includes(role));

    console.log("user role", userRoles);
    console.log("allowed roles", allowedRoles);
    console.log("does user have role?", hasRole);

    if (!hasRole) {
      router.navigate(['/forbidden']);
      return false;
    }
  }

  return true;
};
