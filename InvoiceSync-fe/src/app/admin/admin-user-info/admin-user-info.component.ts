import {Component, OnInit} from '@angular/core';
import {UserBasicInfoComponent} from "../user-basic-info/user-basic-info.component";
import {initFlowbite} from "flowbite";
import {ActivatedRoute} from "@angular/router";
import {UserFeaturesComponent} from "../user-features/user-features.component";

@Component({
  selector: 'app-admin-user-info',
  imports: [
    UserBasicInfoComponent,
    UserFeaturesComponent
  ],
  templateUrl: './admin-user-info.component.html',
  styleUrl: './admin-user-info.component.css'
})
export class AdminUserInfoComponent implements OnInit {
  userId!: string;

  constructor(
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.userId = params.get('id') || '';
    });
    initFlowbite();
  }

}
