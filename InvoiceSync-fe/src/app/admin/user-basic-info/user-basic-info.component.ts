import {Component, Input, OnInit} from '@angular/core';
import {initFlowbite} from "flowbite";
import {FormsModule} from "@angular/forms";
import {AdminService} from "../../core/services/admin.service";
import {NgClass} from "@angular/common";



@Component({
  selector: 'app-user-basic-info',
  imports: [
    FormsModule,
    NgClass
  ],
  templateUrl: './user-basic-info.component.html',
  styleUrl: './user-basic-info.component.css'
})
export class UserBasicInfoComponent implements OnInit {


  @Input() userId!: string;
  user = {
    id: 'd7eab7ea-7e5f-40f5-aa3b-23f817b958cc',
    fullName: 'Jese Leos',
    username: 'jese.leos',
    email: 'jese@example.com',
    role: 'ADMIN',
    createdOn: '2024-05-10T12:00:00',
    subscriptionPlan: {
      name: 'Pro',
      renewalDate: '2025-12-01'
    },
    usage: {
      invoicesUsed: 120,
      exportsUsed: 8,
      storageUsed: 450
    },
    limits: {
      invoicesLimit: 500,
      exportsLimit: 20,
      storageLimit: 1000
    }
  };

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
