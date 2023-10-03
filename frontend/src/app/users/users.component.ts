import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';

export interface UserData {
  userName: string;
  displayName: string;
  email: string;
  phoneNumber: string;
  profilePicture: string;
  roles: string[];
}

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent {
  displayedColumns: string[] = [
    'userName',
    'displayName',
    'email',
    'phoneNumber',
    'profilePicture',
    'roles',
  ];

  dataSource: MatTableDataSource<UserData>;

  constructor() {
    // Dummy data
    const users: UserData[] = [
      {
        userName: 'user1',
        displayName: 'User One',
        email: 'user1@example.com',
        phoneNumber: '123-456-7890',
        profilePicture: 'avatar1.jpg',
        roles: ['Admin', 'User'],
      },
      // Add more dummy data as needed
    ];

    this.dataSource = new MatTableDataSource(users);
  }
}
