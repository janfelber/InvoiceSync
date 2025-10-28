import {Component, OnInit} from '@angular/core';
import {CurrencyPipe, DatePipe, NgForOf, NgIf} from "@angular/common";
import {PageResponseUsers} from "../../pages/page-response-users";
import {AdminService} from "../../core/services/admin.service";
import {RouterLink} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";

@Component({
  selector: 'app-admin-users',
  imports: [
    NgForOf,
    FormsModule,
    DatePipe,
    RouterLink
  ],
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.css'
})
export class AdminUsersComponent implements OnInit{

  constructor(private adminService: AdminService) {
  }

  public page: number = 0;
  public size: number = 10;

  protected readonly Math = Math;

  public usersResponse: PageResponseUsers = {
    content: []
  };


  ngOnInit(): void {
      this.onFetchAllUsers();
  }

  onFetchAllUsers(): void {
    this.adminService.findAllUsers({
      page: this.page,
      size: this.size
      }).then(response => {
        this.usersResponse = response.data;
    })
  }

  goToPreviousPage() {
    this.page--;
    this.onFetchAllUsers();
  }

  goToPage(page: number) {
    this.page = page;
    this.onFetchAllUsers();
  }

  goToNextPage() {
    this.page++;
    this.onFetchAllUsers();
  }

  get IsLastPage(): boolean {
    return this.page == this.usersResponse.totalPages as number - 1
  }
}
