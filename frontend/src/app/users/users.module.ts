import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersComponent } from './users.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';



@NgModule({
  declarations: [
    UsersComponent
  ],
  imports: [
    MatTableModule,
    MatPaginatorModule,
    CommonModule
  ]
})
export class UsersModule { }
