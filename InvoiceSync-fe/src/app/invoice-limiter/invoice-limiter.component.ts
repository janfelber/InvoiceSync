import { Component } from '@angular/core';
import {RouterLink} from "@angular/router";
import {NgOptimizedImage} from "@angular/common";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-invoice-limiter',
    imports: [
        RouterLink,
        FormsModule
    ],
  templateUrl: './invoice-limiter.component.html',
  styleUrl: './invoice-limiter.component.css'
})
export class InvoiceLimiterComponent {

}
