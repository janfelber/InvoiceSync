import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe, NgClass, NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../core/services/user.service';
import { UserInfoResponse } from '../pages/settings/user-info.response';
import { initFlowbite } from 'flowbite';
import { TranslateModule } from '@ngx-translate/core';
import { AppLanguage, LanguageService, SUPPORTED_LANGUAGES } from '../core/services/language.service';
import { ThemeService } from '../core/services/theme.service';
import { SubscriptionService } from '../core/services/subscription.service';
import { SimpleSelectComponent, SelectOption } from '../shared/simple-select/simple-select.component';

@Component({
  selector: 'app-user-settings',
  imports: [
    RouterLink,
    NgForOf,
    NgIf,
    NgClass,
    DatePipe,
    FormsModule,
    TranslateModule,
    SimpleSelectComponent,
  ],
  templateUrl: './user-settings.component.html',
  styleUrl: './user-settings.component.css'
})
export class UserSettingsComponent implements OnInit {

  public userInfo: UserInfoResponse | null = null;
  public loadingUserInfo = false;
  readonly languages: SelectOption[] = SUPPORTED_LANGUAGES.map(l => ({ value: l.code, label: l.label }));

  userLimit = {
    invoiceCreateLimit: 0,
    invoiceCreateUsed: 0,
    invoiceExportLimit: 0,
    invoiceExportUsed: 0,
    receiptExportLimit: 0,
    receiptExportUsed: 0,
  };

  constructor(
    private userService: UserService,
    private subscriptionService: SubscriptionService,
    public languageService: LanguageService,
    public themeService: ThemeService,
  ) {}

  switchLanguage(code: AppLanguage): void {
    this.languageService.use(code);
  }

  ngOnInit(): void {
    this.getCurrentUserInfo();
    this.subscriptionService.getUserLimits().then(r => this.userLimit = r.data);
  }

  getCurrentUserInfo() {
    this.loadingUserInfo = true;
    this.userService.getCurrentUserInfo().then(response => {
      setTimeout(() => {
        this.userInfo = response.data;
        this.loadingUserInfo = false;
        initFlowbite();
      }, 500);
    });
  }

  getInitials(name: string): string {
    if (!name) return '';
    const words = name.trim().split(' ');
    if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
    return (words[0][0] + words[1][0]).toUpperCase();
  }

  usagePct(used: number, limit: number): number {
    if (!limit) return 0;
    return Math.min(100, Math.round((used / limit) * 100));
  }
}
