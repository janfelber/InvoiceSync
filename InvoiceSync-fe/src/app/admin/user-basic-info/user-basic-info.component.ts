import {Component, Input, OnInit} from '@angular/core';
import {initFlowbite} from "flowbite";
import {FormsModule} from "@angular/forms";
import {AdminService} from "../../core/services/admin.service";

@Component({
  selector: 'app-user-basic-info',
  imports: [
    FormsModule
  ],
  templateUrl: './user-basic-info.component.html',
  styleUrl: './user-basic-info.component.css'
})
export class UserBasicInfoComponent implements OnInit {
  @Input() userId!: string;
  user: any = {};

  constructor(
    private adminService: AdminService
  ) {
  }

  ngOnInit(): void {
    initFlowbite();
    this.getUserInfo();
  }

  getUserInfo() {
    this.adminService.getUserInfo(
      this.userId
    ).then(response => {
      this.user = response.data;
    })
  }

}
