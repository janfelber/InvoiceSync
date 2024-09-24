import { Component, EventEmitter, HostListener, OnInit, Output } from '@angular/core';
import { navbarData } from './nav-data';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { animate, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css',
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({opacity:0}),
        animate('350ms', 
          style({opacity:1})
        )
      ]),
      transition(':leave', [
        style({opacity:0}),
        animate('350ms', 
          style({opacity:0})
        )
      ])
    ])
  ]
})
export class SidenavComponent{
  navData = navbarData;

}
