import { Component } from '@angular/core';
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-error',
  imports: [
    RouterLink
  ],
  template: `
    <div
      class="flex flex-col items-center justify-center min-h-screen bg-gray-100 text-center md:text-left px-4 py-8 gap-8">

      <section>
        <div class="py-8 px-4 mx-auto max-w-screen-xl lg:py-16 lg:px-6">
          <div class="mx-auto max-w-screen-sm text-center">
            <h1 class="mb-4 text-7xl tracking-tight font-extrabold lg:text-9xl text-primary-600">403</h1>
            <p class="mb-4 text-3xl tracking-tight font-bold text-gray-900 md:text-4xl">Prístup zakázaný</p>
            <p class="mb-4 text-lg font-light text-gray-500">
              Nemáte oprávnenie na zobrazenie tejto stránky. Ak si myslíte, že ide o chybu, kontaktujte administrátora.
            </p>
            <a routerLink="/home"
               class="inline-flex items-center justify-center text-white bg-blue-600 hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-6 py-3 text-center transition-colors duration-200 my-4">
              Späť domov
            </a>
          </div>
        </div>
      </section>

    </div>

  `
})
export class ErrorComponent {}
