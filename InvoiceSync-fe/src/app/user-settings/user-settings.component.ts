import {Component, OnInit} from '@angular/core';
import {RouterLink} from "@angular/router";
import {NgForOf, NgIf} from "@angular/common";
import {UserService} from "../core/services/user.service";
import {UserInfoResponse} from "../pages/settings/user-info.response";
import {initFlowbite} from "flowbite";
import {TranslateModule} from "@ngx-translate/core";
import {AppLanguage, LanguageService, SUPPORTED_LANGUAGES} from "../core/services/language.service";

@Component({
  selector: 'app-user-settings',
  imports: [
    RouterLink,
    NgForOf,
    NgIf,
    TranslateModule,
  ],
  templateUrl: './user-settings.component.html',
  styleUrl: './user-settings.component.css'
})
export class UserSettingsComponent implements OnInit {

  public userInfo: UserInfoResponse | null = null;
  public loadingUserInfo = false;

  readonly languages = SUPPORTED_LANGUAGES;

  constructor(
    private userService: UserService,
    public languageService: LanguageService,
  ) {}

  switchLanguage(code: AppLanguage): void {
    this.languageService.use(code);
  }

  ngOnInit(): void {
    this.getCurrentUserInfo()
  }

  getCurrentUserInfo() {
    this.loadingUserInfo = true;

    this.userService.getCurrentUserInfo().then(response => {
      setTimeout(() => {
        this.userInfo = response.data;
        this.loadingUserInfo = false;

        // až teraz inicializuj Flowbite
        initFlowbite();
      }, 500);
    });
  }

  getInitials(name: string): string {
    if (!name) return '';
    const words = name.trim().split(' ');
    if (words.length === 1) {
      return words[0].substring(0, 2).toUpperCase();
    }
    return (words[0][0] + words[1][0]).toUpperCase();
  }


}
