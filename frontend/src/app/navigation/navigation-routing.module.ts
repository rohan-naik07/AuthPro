import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from '../app.component';
import { RealmsComponent } from '../realms/realms.component';
import { UsersComponent } from '../users/users.component';
import { SettingsComponent } from '../settings/settings.component';
import { NavigationComponent } from './navigation.component';

const routes: Routes = [
  {
    path : "",
    component : NavigationComponent,
    children : [
      {
        path: "", 
        redirectTo : "realms",
        pathMatch : 'full'
    },{
        path : "realms",
        component : RealmsComponent
    },{
        path : "users",
        component : UsersComponent
    },{
        path : "settings",
        component : SettingsComponent
    }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NavigationRoutingModule { }
