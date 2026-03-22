import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-google-integration',
  imports: [CommonModule, RouterLink],
  templateUrl: './google-integration.component.html',
})
export class GoogleIntegrationComponent {
  gmailConnected = false;
  driveConnected = false;

  connectGmail() {
    // TODO: OAuth flow
  }

  disconnectGmail() {
    this.gmailConnected = false;
  }

  connectDrive() {
    // TODO: OAuth flow
  }

  disconnectDrive() {
    this.driveConnected = false;
  }

  get connectedCount(): number {
    return [this.gmailConnected, this.driveConnected].filter(Boolean).length;
  }
}
