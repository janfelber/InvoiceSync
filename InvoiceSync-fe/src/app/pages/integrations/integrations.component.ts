import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-integrations',
  imports: [CommonModule, RouterLink],
  templateUrl: './integrations.component.html',
})
export class IntegrationsComponent implements OnInit {
  constructor(private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['google'] === 'connected') {
        this.router.navigate(['/web/integrations/google'], { replaceUrl: true });
      }
    });
  }

  goTo(path: string) {
    this.router.navigate([path]);
  }
}
