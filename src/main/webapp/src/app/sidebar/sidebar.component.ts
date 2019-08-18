import {Component, OnInit} from '@angular/core';
import {UserService} from "../shared/services/user.service";
import {NavigationEnd, Router} from "@angular/router";
import {Project} from "../shared/model/project";
import {Observable} from "rxjs";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  currentProject: Project;
  userName: Observable<String>;
  userRole: Observable<String>;

  constructor(private userService: UserService, private router: Router) {
    this.userName = this.userService.currentUser.map(x => x.name);
    this.userRole = this.userService.currentUser.map(x => x.role? x.role.name : "");
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd && event.url == '/project') {
        this.currentProject = history.state;
      }
    })
  }

  ngOnInit() {
  }

  onProjectOpen(): boolean {
    return this.router.url === '/project'
  }

  newTest(): void {
    this.router.navigate(['/new_test'], {state: this.currentProject});
  }
}
