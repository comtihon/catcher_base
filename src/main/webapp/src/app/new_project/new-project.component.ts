import {Component, OnInit} from '@angular/core';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/merge';
import 'rxjs/add/operator/filter';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AlertService} from "../shared/services/alert.service";
import {TeamService} from "../shared/services/team.service";

@Component({
  selector: 'app-forms',
  templateUrl: './new-project.component.html'
})
export class NewProjectComponent implements OnInit {
  newProjectForm: FormGroup;
  submitted = false;
  waiting = false;

  teamsModel = [];
  teams = [];

  constructor(private formBuilder: FormBuilder,
              private alertService: AlertService,
              private teamService: TeamService) {
  }

  ngOnInit() {
    this.newProjectForm = this.formBuilder.group({
      projectName: ['', Validators.required]
    });
    this.teams = this.teamService.teamsValue.map(team => team.name);
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

    this.waiting = true;
  }
}
