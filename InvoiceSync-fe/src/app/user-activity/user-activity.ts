import {Component, OnInit} from '@angular/core';
import {DatePipe, NgClass, NgForOf} from "@angular/common";
import {initFlowbite} from "flowbite";
import {AdminService} from "../core/services/admin.service";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-user-activity',
  imports: [
    DatePipe,
    NgForOf,
    NgClass,
    RouterLink
  ],
  templateUrl: './user-activity.html',
  styleUrl: './user-activity.css',
})
export class UserActivity implements OnInit {

  constructor(private adminService: AdminService) {

  }

  ngOnInit(): void {
    this.getUserActivities();
    initFlowbite();
  }

  test: any;

  getUserActivities() {
    this.adminService.getUsersActivity().then(response => {
      this.test = response.data;
      console.log(this.test);
    })
  }

}
