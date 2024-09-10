import { Component } from '@angular/core';
import { WelcomeContentComponent } from '../welcome-content/welcome-content.component';
import { LoginFormComponent } from '../login-form/login-form.component';
import { AxiosService } from '../axios.service';
import { CommonModule } from '@angular/common';
import { ButtonsComponent } from '../buttons/buttons.component';
import { AuthContentComponent } from '../auth-content/auth-content.component';
import { HeaderComponent } from '../header/header.component';
import { SidenavComponent } from '../sidenav/sidenav.component';
import { BodyComponent } from '../body/body.component';


@Component({
  selector: 'app-content',
  standalone: true,
  imports: [WelcomeContentComponent, LoginFormComponent, CommonModule, ButtonsComponent, AuthContentComponent, HeaderComponent, SidenavComponent, BodyComponent],
  templateUrl: './content.component.html',
  styleUrl: './content.component.css'
})
export class ContentComponent {
  componentToShow: string = "welcome";


  constructor(private axiosSerice: AxiosService) { }

  showComponent(componentToShow: string): void {
    this.componentToShow = componentToShow;
  }

	onLogin(input: any): void {
		this.axiosSerice.request(
		    "POST",
		    "/login",
		    {
		        login: input.login,
		        password: input.password
		    }).then(
		    response => {
		        this.axiosSerice.setAuthToken(response.data.token);
		        this.componentToShow = "invoices";
		    }).catch(
		    error => {
		        this.axiosSerice.setAuthToken(null);
		        this.componentToShow = "welcome";
		    }
		);

	}
}
