import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {AuthGuard} from "./shared/guards/auth.guard";
import {HomeComponent} from "./home/home.component";
import {RegisterComponent} from "./register/register.component";
import {DashboardComponent} from "./dashboard/dashboard.component";


const routes: Routes = [
  {path: '', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard]},

  {path: '**', redirectTo: '/home'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
