import { Component } from '@angular/core';
import {Router} from '@angular/router';

import {User} from "./shared/model/user";
import {AuthService} from "./shared/services/auth.service";
import {UserService} from "./shared/services/user.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  currentUser: User | null;
  title = 'webapp';

  constructor(
      private router: Router,
      private authenticationService: AuthService,
      private userService: UserService
    ) {
      this.userService.currentUser.subscribe(x => this.currentUser = x); // TODO ?
    }
}
