import {Component, OnInit} from '@angular/core';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/merge';
import 'rxjs/add/operator/filter';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AlertService} from "../shared/services/alert.service";
import {TeamService} from "../shared/services/team.service";
import {ProjectService} from "../shared/services/project.service";
import {Team} from "../shared/model/team";
import {Router} from "@angular/router";
import {Project} from "../shared/model/project";

@Component({
  selector: 'app-forms',
  templateUrl: './new-project.component.html'
})
export class NewProjectComponent implements OnInit {
  newProjectForm: FormGroup;
  submitted = false;
  waiting = false;
  project: Project;

  teams = [];

  constructor(private formBuilder: FormBuilder,
              private alertService: AlertService,
              private teamService: TeamService,
              private router: Router,
              private projectService: ProjectService) {
  }

  ngOnInit() {
    this.newProjectForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: [],
      remotePath: [],
      localPath: [],
      tests: [[]],
      teams: []
    });
    this.teams = this.teamService.teamsValue.map(team => team.name);
    this.project = history.state.project;
    if(this.project) {
      this.newProjectForm.controls.name.setValue(this.project.name);
      this.newProjectForm.controls.description.setValue(this.project.description);
      this.newProjectForm.controls.remotePath.setValue(this.project.remotePath);
      this.newProjectForm.controls.localPath.setValue(this.project.localPath);
      this.newProjectForm.controls.teams.setValue(this.project.teams.map(team => team.name));
    }
  }

  get f() {
    return this.newProjectForm.controls;
  }

  onSubmit() {
    this.submitted = true;

    // reset alerts on submit
    this.alertService.clear();

    // stop here if form is invalid
    if (this.newProjectForm.invalid) {
      return;
    }

    // TODO detect if new team were created teamsToAdd vs teams
    let project = this.newProjectForm.value;
    if (project.teams == null || project.teams.length == 0) {
      this.alertService.error('At least one team required');
      return;
    }

    this.waiting = true;

    project.teams = project.teams.map(team => new Team(team));

    this.projectService.newProject(project)
      .subscribe(() => {
        this.waiting = false;
        // TODO navigate to project's page instead!
        // TODO update team service after successful project creation?
        this.router.navigate(['/projects']);
      }, error => {
        this.alertService.error(error);
      })
  }
}
