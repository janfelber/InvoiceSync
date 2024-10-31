import { Component } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatIcon } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';

/**
 * @deprecated DO NOT USE THIS COMPONENT.
 */
@Component({
  selector: 'app-header-component',
  standalone: true,
  imports: [MatIcon, MatToolbarModule],
  templateUrl: './header-component.component.html',
  styleUrl: './header-component.component.css'
})
export class HeaderComponentComponent {

}
