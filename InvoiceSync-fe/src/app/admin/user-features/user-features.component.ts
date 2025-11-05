import {Component, Input, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {AdminService} from "../../core/services/admin.service";
import {initFlowbite} from "flowbite";

@Component({
  selector: 'app-user-features',
  imports: [
    FormsModule,
    NgForOf
  ],
  templateUrl: './user-features.component.html',
  styleUrl: './user-features.component.css'
})
export class UserFeaturesComponent implements OnInit {
  @Input() userId!: string;

  constructor(
    private adminService: AdminService,
  ) {
  }

  features: any;

  ngOnInit(): void {
    initFlowbite();
    this.getFeatures();
  }

  async getFeatures() {
    const response = await this.adminService.getFeatures(
      this.userId
    );
    this.features = response.data;
  }

  updateFeatures() {
    const activeIds = this.features
      .filter((f: any) => f.enabled)
      .map((f: any) => f.code);

    this.adminService.updateUserFeatures(this.userId, activeIds)
      .then(() => this.getFeatures);
  }

}
