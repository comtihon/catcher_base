import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';

import {LoginComponent} from './login/login.component';
import {JwtInterceptor} from "./shared/interceptor/jwt.interceptor";
import {ErrorInterceptor} from "./shared/interceptor/error.interceptor";
import {AlertComponent} from "./components/alert.component";
import {HomeComponent} from "./home/home.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {RegisterComponent} from './register/register.component';
import {NavbarComponent} from "./navbar/navbar.component";
import {SidebarComponent} from "./sidebar/sidebar.component";
import {FooterComponent} from "./components/footer.component";
import {ProjectsComponent} from "./projects/projects.component";
import {ChartsModule} from "ng2-charts";
import {NewProjectComponent} from "./new-project/new-project.component";
import {TagInputModule} from "ngx-chips";
import {ProjectComponent} from './project/project.component';
import {TestComponent} from './test/test.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {AceEditorModule} from "ng2-ace-editor";


@NgModule({
  declarations: [
    AppComponent,
    AlertComponent,
    LoginComponent,
    RegisterComponent,
    NavbarComponent,
    SidebarComponent,
    FooterComponent,
    ProjectsComponent,
    HomeComponent,
    NewProjectComponent,
    ProjectComponent,
    TestComponent
  ],
  imports: [
    TagInputModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule,
    ChartsModule,
    FormsModule,
    NgbModule,
    AceEditorModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
