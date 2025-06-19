import {APP_INITIALIZER, ApplicationConfig} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient} from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideAnimations } from '@angular/platform-browser/animations';

import { provideToastr } from 'ngx-toastr';
import {KeycloakService} from "./core/keycloak/keycloak.service";
import {HttpTokenInterceptor} from "./core/interceptor/http-token.interceptor";

export function kcFactory(kcService: KeycloakService) {
  return () => kcService.init();
}


export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    provideAnimationsAsync(),
    BrowserAnimationsModule,
    provideAnimations(),
    provideToastr(),
    {
      provide: APP_INITIALIZER,
      deps:[KeycloakService],
      useFactory: kcFactory,
      multi: true
    }
  ]
};
