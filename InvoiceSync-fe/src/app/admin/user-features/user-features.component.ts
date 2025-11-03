import {Component, Input, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {AdminService} from "../../core/services/admin.service";

@Component({
  selector: 'app-user-features',
  imports: [
    FormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './user-features.component.html',
  styleUrl: './user-features.component.css'
})
export class UserFeaturesComponent implements OnInit{
  @Input() userId!: string;
  private selectedFeatures: any;

  constructor(
    private adminService: AdminService,
  ) {}

    features: any;

    ngOnInit(): void {
        this.getFeatures();
    }

    async getFeatures() {
      const response = await this.adminService.getFeatures(
        this.userId
      );
      this.features = response.data;
      console.log(this.features);
    }

}
