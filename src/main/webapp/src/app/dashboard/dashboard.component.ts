import {Component, OnInit} from '@angular/core';
import {ViewEncapsulation} from '@angular/core';
import {ProjectService} from "../shared/services/project.service";
import {Project} from "../shared/model/project";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['../app.component.scss', './dashboard.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DashboardComponent implements OnInit {

  projects: Project[];

  constructor(private projectService: ProjectService) {
    this.projects = projectService.projectsValue;
  }

  ngOnInit() {
    this.projectService.loadProjects()
      .subscribe(() => {
        this.projects = this.projectService.projectsValue;
      })
  }

}
