import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {User} from "../shared/model/user";
import {SystemService} from "../shared/services/system.service";
import {SystemInfo} from "../shared/model/systemInfo";
import {UserService} from "../shared/services/user.service";
import {Label, MultiDataSet} from "ng2-charts";
import {ChartType} from "chart.js";
import {ProjectService} from "../shared/services/project.service";
import {TeamService} from "../shared/services/team.service";
import {RoleService} from "../shared/services/role.service";
import {Observable} from "rxjs";


@Component({
  selector: 'app-dashboard',
  templateUrl: 'home.component.html',
  styleUrls: ['../app.component.scss', './home.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class HomeComponent implements OnInit {
  systemInfo: SystemInfo;
  isAdmin: Observable<boolean>;

  public chartLabels: Label[] = ['Passed', 'Failed', 'Running', 'Aborted'];
  public chartDatasets: MultiDataSet = [
    [12, 1, 4, 2]
  ];
  public chartType: ChartType = 'doughnut';
  private chartColors = [
    {
      backgroundColor: [
        'rgb(27,114,31)',
        'rgb(183,53,40)',
        'rgb(148,147,39)',
        'rgb(129,127,125)',
      ]
    }
  ];
  // TODO set empty true only after loading finished.
  public emptyStatistics: boolean = true;

  constructor(private userService: UserService,
              private systemService: SystemService,
              private projectService: ProjectService,
              private teamService: TeamService,
              private roleService: RoleService) {
    this.isAdmin = this.userService.currentUser.map(x => x ? x.isAdmin() : false);
    this.systemService.systemInfo.subscribe(x => this.systemInfo = x);
  }

  ngOnInit() {
    this.systemService.loadSystemInfo()
      .subscribe(() => {
        this.systemInfo = this.systemService.systemInfoValue;
      }, error => {
        // TODO handle error
      });
    this.projectService.loadProjects()
      .subscribe(() => {
        let statistics = this.projectService.gatherStatistics();
        this.emptyStatistics = statistics.reduce((a, b) => a + b, 0) == 0;
        this.chartDatasets = [statistics]
      });
    this.roleService.loadRoles();
    this.teamService.loadTeams();
  }

  public chartClicked(e: any): void {
  }

  public chartHovered(e: any): void {
  }
}
