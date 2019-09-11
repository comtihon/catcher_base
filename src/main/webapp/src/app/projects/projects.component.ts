import {Component, OnInit} from '@angular/core';
import {ViewEncapsulation} from '@angular/core';
import {ProjectService} from "../shared/services/project.service";
import {Project} from "../shared/model/project";
import {Router} from "@angular/router";

@Component({
  selector: 'app-dashboard',
  templateUrl: './projects.component.html',
  styleUrls: ['../app.component.scss', './projects.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProjectsComponent implements OnInit {

  projects: Project[];

  constructor(private projectService: ProjectService, public router: Router) {
    this.projects = projectService.projectsValue;
    if (this.projects.length == 0) { // this form was loaded bypassing home (page refresh)
      projectService.loadProjects().subscribe(projects => this.projects = projects)
    }
  }

  ngOnInit() {
  }

  navigate(project: Project): void {
    this.router.navigate(['/project'], {state: {project: project}});
  }

}
