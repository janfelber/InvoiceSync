import {Component, OnInit} from '@angular/core';
import {
  DataTransferExcelInspectComponent
} from "../pages/data-transfer-excel-inspect/data-transfer-excel-inspect.component";
import {DataTransferMappingComponent} from "../pages/data-transfer-mapping/data-transfer-mapping.component";
import {RouterLink} from "@angular/router";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {UserService} from "../core/services/user.service";
import {UserInfoResponse} from "../pages/settings/user-info.response";
import {initFlowbite} from "flowbite";
import {TranslatePipe, TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-user-settings',
  imports: [
    RouterLink,
    NgForOf,
    NgIf,
    TranslatePipe
  ],
  templateUrl: './user-settings.component.html',
  styleUrl: './user-settings.component.css'
})
export class UserSettingsComponent implements OnInit {

  public userInfo: UserInfoResponse | null = null;
  public loadingUserInfo = false

  public currentLang: string = 'sk';

  constructor(
    private userService: UserService,
    private translate: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.getCurrentUserInfo()
    this.currentLang = localStorage.getItem('lang') || 'sk';
    this.translate.use(this.currentLang);
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

  changeLang(lang: string) {
    this.currentLang = lang;       // aktualizuje dropdown
    this.translate.use(lang);      // prepne jazyk
    localStorage.setItem('lang', lang);
  }


}
