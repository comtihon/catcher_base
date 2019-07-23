import {Component, OnInit} from '@angular/core';
import {User} from "../shared/model/user";
import {UserService} from "../shared/services/user.service";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  currentUser: User;

  constructor(private userService: UserService) {
    this.currentUser = userService.currentUserValue;
  }

  ngOnInit() {
  }

}
