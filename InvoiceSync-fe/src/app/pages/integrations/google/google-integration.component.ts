import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { GoogleIntegrationService } from '../../../core/services/google-integration.service';

@Component({
  selector: 'app-google-integration',
  imports: [CommonModule, RouterLink],
  templateUrl: './google-integration.component.html',
})
export class GoogleIntegrationComponent implements OnInit {
  gmailConnected = false;
  gmailEmail = '';
  driveConnected = false;
  loading = true;

  constructor(
    private googleService: GoogleIntegrationService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.loadStatus();
  }

  private loadStatus() {
    this.loading = true;
    this.googleService.getStatus().then(status => {
      this.gmailConnected = status.connected;
      this.gmailEmail = status.email;
      this.loading = false;
    }).catch(() => {
      this.loading = false;
    });
  }

  connectGmail() {
    this.googleService.getAuthUrl().then(res => {
      window.location.href = res.url;
    });
  }

  disconnectGmail() {
    this.googleService.disconnect().then(() => {
      this.gmailConnected = false;
      this.gmailEmail = '';
    });
  }

  connectDrive() {
    // TODO: Google Drive OAuth
  }

  disconnectDrive() {
    this.driveConnected = false;
  }

  get connectedCount(): number {
    return [this.gmailConnected, this.driveConnected].filter(Boolean).length;
  }
}
