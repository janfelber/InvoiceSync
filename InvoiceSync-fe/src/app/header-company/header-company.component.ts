import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
  selector: 'app-header-company',
  standalone: true,
  imports: [
    MatToolbarModule
  ],
  templateUrl: './header-company.component.html',
  styleUrl: './header-company.component.css'
})
export class HeaderCompanyComponent {

}
