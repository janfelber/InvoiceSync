import { Component } from '@angular/core';
import { AxiosService } from '../axios.service';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { SidenavComponent } from '../sidenav/sidenav.component';
import { BodyComponent } from '../body/body.component';
import { HeaderCompanyComponent } from '../header-company/header-company.component';


/**
 * @deprecated DO NOT USE THIS COMPONENT.
 */
@Component({
  selector: 'app-content',
  standalone: true,
  imports: [ CommonModule, HeaderComponent, SidenavComponent, BodyComponent, HeaderCompanyComponent],
  templateUrl: './content.component.html',
  styleUrl: './content.component.css'
})

export class ContentComponent {

}
