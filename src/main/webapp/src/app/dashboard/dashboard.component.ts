import {Component, OnInit} from '@angular/core';
import {ViewEncapsulation} from '@angular/core';
import {ProjectService} from "../shared/services/project.service";
import {Project} from "../shared/model/project";
import {Router} from "@angular/router";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['../app.component.scss', './dashboard.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DashboardComponent implements OnInit {

  projects: Project[];

  constructor(private projectService: ProjectService, public router: Router) {
    this.projects = projectService.projectsValue;
  }

  ngOnInit() {
  }

  navigate(project: Project): void {
    this.router.navigate(['/project'], {state: project});
  }

}
