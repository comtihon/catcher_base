import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {User} from "../shared/model/user";
import {SystemService} from "../shared/services/system.service";
import {SystemInfo} from "../shared/model/systemInfo";
import {UserService} from "../shared/services/user.service";
import {Label, MultiDataSet} from "ng2-charts";
import {ChartType} from "chart.js";
import {Observable} from "rxjs";


@Component({
  selector: 'app-dashboard',
  templateUrl: 'home.component.html',
  styleUrls: ['../app.component.scss', './home.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class HomeComponent implements OnInit {
  currentUser: Observable<User>;
  systemInfo: SystemInfo;

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

  constructor(private userService: UserService,
              private systemService: SystemService) {
    this.currentUser = this.userService.currentUser;
    this.systemInfo = this.systemService.systemInfoValue
  }

  ngOnInit() {
    this.systemService.loadSystemInfo()
      .subscribe(() => {
        this.systemInfo = this.systemService.systemInfoValue;
      }, error => {
        // TODO handle error
      })
  }

  public chartClicked(e: any): void {
  }

  public chartHovered(e: any): void {
  }
}
