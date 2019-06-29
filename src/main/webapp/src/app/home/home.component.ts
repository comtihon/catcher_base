import {Component, OnInit} from '@angular/core';
import {User} from "../shared/model/user";
import {AuthService} from "../shared/services/auth.service";
import {SystemService} from "../shared/services/system.service";
import {SystemInfo} from "../shared/model/systemInfo";


@Component({templateUrl: 'home.component.html'})
export class HomeComponent implements OnInit {
  currentUser: User;
  systemInfo: SystemInfo;

  constructor(private authenticationService: AuthService,
              private systemService: SystemService) {
    this.currentUser = this.authenticationService.currentUserValue;
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
