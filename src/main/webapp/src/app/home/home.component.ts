import {Component, OnInit} from '@angular/core';
import {User} from "../shared/model/user";
import {SystemService} from "../shared/services/system.service";
import {SystemInfo} from "../shared/model/systemInfo";
import {UserService} from "../shared/services/user.service";


@Component({templateUrl: 'home.component.html'})
export class HomeComponent implements OnInit {
  currentUser: User;
  systemInfo: SystemInfo;

  constructor(private userService: UserService,
              private systemService: SystemService) {
    this.currentUser = this.userService.currentUserValue;
    this.systemInfo = this.systemService.systemInfoValue
  }

  ngOnInit() {
    this.systemService.loadSystemInfo()
      .subscribe(() => {
        this.systemInfo = this.systemService.systemInfoValue;
      }, error => {
        // TODO handle error
      })
  }
}
