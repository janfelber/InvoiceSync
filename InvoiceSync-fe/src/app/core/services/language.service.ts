import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

export type AppLanguage = 'sk' | 'en' | 'cs';

export const SUPPORTED_LANGUAGES: { code: AppLanguage; label: string }[] = [
  { code: 'sk', label: 'Slovenčina' },
  { code: 'en', label: 'English' },
  { code: 'cs', label: 'Čeština' },
];

const STORAGE_KEY = 'app_language';

@Injectable({ providedIn: 'root' })
export class LanguageService {

  constructor(private translate: TranslateService) {}

  init(): void {
    const saved = localStorage.getItem(STORAGE_KEY) as AppLanguage | null;
    const lang = saved && this.isSupported(saved) ? saved : 'sk';
    this.translate.setDefaultLang('sk');
    this.translate.use(lang);
  }

  use(lang: AppLanguage): void {
    this.translate.use(lang);
    localStorage.setItem(STORAGE_KEY, lang);
  }

  get current(): AppLanguage {
    return this.translate.currentLang as AppLanguage;
  }

  get supported() {
    return SUPPORTED_LANGUAGES;
  }

  private isSupported(lang: string): lang is AppLanguage {
    return SUPPORTED_LANGUAGES.some(l => l.code === lang);
  }
}
