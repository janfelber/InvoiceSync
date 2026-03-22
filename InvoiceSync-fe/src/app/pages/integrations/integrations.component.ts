import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-integrations',
  imports: [CommonModule, RouterLink],
  templateUrl: './integrations.component.html',
})
export class IntegrationsComponent {
  constructor(private router: Router) {}

  goTo(path: string) {
    this.router.navigate([path]);
  }
}
