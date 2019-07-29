import {Component} from '@angular/core';
import {Router} from '@angular/router';


import {User} from "./shared/model/user";
import {AuthService} from "./shared/services/auth.service";
import {UserService} from "./shared/services/user.service";

@Component({selector: 'app', templateUrl: 'app.component.html'})
export class AppComponent {
  currentUser: User;

  constructor(
    private router: Router,
    private authenticationService: AuthService,
    private userService: UserService
  ) {
    this.userService.currentUser.subscribe(x => this.currentUser = x);
  }
}
