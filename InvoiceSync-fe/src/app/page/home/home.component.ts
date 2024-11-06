import { Component } from '@angular/core';
import {ContentComponent} from "../../content/content.component";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true,
  imports: [
    ContentComponent
  ]
})
export class HomeComponent {
}
