import {Component, OnInit} from '@angular/core';
import {NgbDropdownConfig} from '@ng-bootstrap/ng-bootstrap';
import {ProjectService} from "../shared/services/project.service";
import {Notification, NotificationType} from "../shared/model/notification";
import {UserService} from "../shared/services/user.service";
import {User} from "../shared/model/user";
import {AuthService} from "../shared/services/auth.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  providers: [NgbDropdownConfig]
})
export class NavbarComponent implements OnInit {
  public sidebarOpened = false;
  notifications: Notification;
  notificationTypes = NotificationType;
  currentUser: User;

  constructor(config: NgbDropdownConfig,
              private projectService: ProjectService,
              private userService: UserService,
              private authService: AuthService) {
    config.placement = 'bottom-right';
//     this.projectService.notifications.subscribe(x => this.notifications = x);
//     this.userService.currentUser.subscribe(x => this.currentUser = x);
  }

  toggleOffcanvas() {
    this.sidebarOpened = !this.sidebarOpened;
    if (this.sidebarOpened) {
      document.querySelector('.sidebar-offcanvas').classList.add('active');
    } else {
      document.querySelector('.sidebar-offcanvas').classList.remove('active');
    }
  }

  ngOnInit() {
//     this.projectService.loadNotifications()
//       .subscribe(() => {
//         this.notifications = this.projectService.notificationsValue;
//       }, error => {
//         // TODO handle error
//         console.error(error)
//       })
  }

  logout() {
    this.authService.logout()
  }
}
