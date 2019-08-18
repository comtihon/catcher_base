import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {AuthGuard} from "./shared/guards/auth.guard";
import {HomeComponent} from "./home/home.component";
import {RegisterComponent} from "./register/register.component";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {NewProjectComponent} from "./new-project/new-project.component";
import {ProjectComponent} from "./project/project.component";
import {TestComponent} from "./test/test.component";


const routes: Routes = [
  {path: '', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'projects', component: DashboardComponent, canActivate: [AuthGuard]},
  {path: 'project', component: ProjectComponent, canActivate: [AuthGuard]}, // TODO dynamic routes /project/project_name
  {path: 'new_project', component: NewProjectComponent, canActivate: [AuthGuard]},
  {path: 'new_test', component: TestComponent, canActivate: [AuthGuard]},

  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
