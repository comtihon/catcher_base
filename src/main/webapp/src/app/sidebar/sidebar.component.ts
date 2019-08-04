import {Component, OnInit} from '@angular/core';
import {User} from "../shared/model/user";
import {UserService} from "../shared/services/user.service";
import {NavigationEnd, Router} from "@angular/router";
import {Project} from "../shared/model/project";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  currentUser: User;
  currentProject: Project;

  constructor(private userService: UserService, private router: Router) {
    this.currentUser = userService.currentUserValue;
    this.router.events.subscribe(event => {
      if(event instanceof NavigationEnd && event.url == '/project') {
        this.currentProject = history.state;
      }
    })
  }

  ngOnInit() {
  }

  onProjectOpen(): boolean {
    return this.router.url === '/project'
  }
}
