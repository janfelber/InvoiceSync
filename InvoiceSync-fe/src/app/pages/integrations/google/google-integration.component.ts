import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink} from '@angular/router';
import {GoogleIntegrationService} from '../../../core/services/google-integration.service';

@Component({
  selector: 'app-google-integration',
  imports: [CommonModule, RouterLink],
  templateUrl: './google-integration.component.html',
})
export class GoogleIntegrationComponent implements OnInit {

  // ── Services ────────────────────────────────────────────────────────────────
  private readonly googleService = inject(GoogleIntegrationService);

  // ── State signals ────────────────────────────────────────────────────────────
  gmailConnected = signal(false);
  gmailEmail = signal('');
  driveConnected = signal(false);
  loading = signal(true);

  connectedCount = computed(() =>
    [this.gmailConnected(), this.driveConnected()].filter(Boolean).length
  );

  // ── Lifecycle hooks ──────────────────────────────────────────────────────────
  ngOnInit(): void {
    this.loadStatus();
  }

  // ── Public methods ───────────────────────────────────────────────────────────
  connectGmail(): void {
    this.googleService.getAuthUrl().subscribe(res => {
      window.location.href = res.url;
    });
  }

  disconnectGmail(): void {
    this.googleService.disconnect().subscribe(() => {
      this.gmailConnected.set(false);
      this.gmailEmail.set('');
    });
  }

  connectDrive(): void {
    // TODO: Google Drive OAuth
  }

  disconnectDrive(): void {
    this.driveConnected.set(false);
  }

  // ── Private methods ──────────────────────────────────────────────────────────
  private loadStatus(): void {
    this.loading.set(true);
    this.googleService.getStatus().subscribe({
      next: status => {
        this.gmailConnected.set(status.connected);
        this.gmailEmail.set(status.email);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
