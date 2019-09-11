import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Project} from "../shared/model/project";
import {Test, TestRun} from "../shared/model/test";
import {ProjectService} from "../shared/services/project.service";
import {ChartDataSets} from "chart.js";
import {Color, Label} from "ng2-charts";
import {AlertService} from "../shared/services/alert.service";
import {TestService} from "../shared/services/test.service";

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {

  project: Project;
  projectData: ChartDataSets[] = null;
  runDates: Label[] = null;
  chartType = 'line';
  loading = true;
  teams = [];

  public lineChartColors: Color[] = [
    { // passed
      backgroundColor: 'rgba(27,114,31)',
      borderColor: 'rgba(148,159,177,1)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    { // failed
      backgroundColor: 'rgba(183,53,40)',
      borderColor: 'red',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    { // running
      backgroundColor: 'rgb(148,147,39)',
      borderColor: 'rgba(77,83,96,1)',
      pointBackgroundColor: 'rgba(77,83,96,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(77,83,96,1)'
    }
  ];

  constructor(public router: Router,
              public projectService: ProjectService,
              private alertService: AlertService,
              private testService: TestService) {
  }

  ngOnInit() {
    this.alertService.clear();
    this.project = history.state.project;
    if (this.project) {
      // TODO do not load project every time it is opened?
      this.projectService.loadProject(this.project.projectId)
        .subscribe(project => {
          this.project = project;
          this.teams = project.teams.map(team => {
              return {
                value: team.name,
                display: team.name,
                readonly: true
              }
            }
          );
          this.loading = false;
          this.formStatistics(project.tests)
        }, error => {
          this.alertService.error(error);
          this.loading = false;
        })
    } else {
      // TODO get state from history?
      this.router.navigate(['/projects'])
    }
  }

  editProject() {
    this.router.navigate(['/new_project'], {state: {project: this.project}});
  }

  stopTest(test: Test) {
    this.testService.stopTest(test);
  }

  startTest(test: Test) {
    this.testService.runTest(test)
      .subscribe(data => {
        // TODO update test status to queued?
        this.alertService.success("Test started");
        console.log(data);
      }, error => {
        this.alertService.error(error);
      })
  }

  editTest(test: Test) {
    this.router.navigate(['/new_test'], {state: {project: this.project, test: test}});
  }

  private formStatistics(tests: Test[]) {
    let data = new Map<number, Array<TestRun>>();
    for (let test of tests) {
      for (let run of test.runs) {
        run.test = test;  // assign a parent
        let runDate = new Date(run.started);
        // TODO need only date (not datetime)
        if (data.has(runDate.getDate())) {  // there is already some runs
          data[runDate.getDate()] = this.selectLatestRun(data[runDate.getDate()], run);
        } else { // no runs - just populate
          data[runDate.getDate()] = [run];
        }
      }
    }
    this.projectData = this.prepareStatistics(data);

    // TODO data dates to string human readable
    this.runDates = Array.from(data.keys()).map(d => new Date(d).toDateString());
  }

  /**
   * Insert current run into runs. If there is already a run for the same test -
   * keep the latest result
   * @param runs runs to insert current in
   * @param current run to be inserted
   */
  private selectLatestRun(runs: Array<TestRun>, current: TestRun) {
    return runs.map(run => {
        if (run.test == current.test) {  // same test - select latest
          if (new Date(current.started) > new Date(run.started))
            return current;
        }
        return run;  // another test or current is older
      }
    )
  }

  /**
   * Split statistics map on passed, failed and running. Aborted are skipped!
   * @param data
   */
  private prepareStatistics(data: Map<number, Array<TestRun>>): ChartDataSets[] {
    // TODO test me
    let passedData = [];
    let failedData = [];
    let runningData = [];
    data.forEach((value, _key, _) => {
      let passed = 0;
      let failed = 0;
      let running = 0;
      for (let run of value) {
        if (run.status == 'FINISHED') passed += 1;
        if (run.status == 'FAILED') failed += 1;
        if (run.status == 'STARTED' || run.status == 'QUEUED') running += 1;
      }
      passedData.push(passed);
      failedData.push(failed);
      runningData.push(running);
    });
    return [
      {data: passedData.slice(-10), label: 'Passed'},
      {data: failedData.slice(-10), label: 'Failed'},
      {data: runningData.slice(-10), label: 'Running'}
    ]
  }
}
