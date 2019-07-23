import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";
import {BehaviorSubject, Observable} from "rxjs";
import {Notification} from "../model/notification";
import {OverallStatistics, Project} from "../model/project";

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  private notificationsSubject: BehaviorSubject<Notification>;
  public notifications: Observable<Notification>;

  private projectsSubject: BehaviorSubject<Project[]>;
  public projects: Observable<Project[]>;

  constructor(private _router: Router, private http: HttpClient) {
    this.notificationsSubject = new BehaviorSubject<Notification>(new Notification());
    this.notifications = this.notificationsSubject.asObservable();

    this.projectsSubject = new BehaviorSubject<Project[]>([]);
    this.projects = this.projectsSubject.asObservable();
  }

  public get notificationsValue(): Notification {
    return this.notificationsSubject.value;
  }

  public get projectsValue(): Project[] {
    return this.projectsSubject.value
  }

  loadNotifications() {
    // TODO should also subscribe to notifications from the BE!
    // TODO implement me
    return this.http.get<any>('/api/v1/system')
      .pipe(map(data => {
        this.notificationsSubject.next(new Notification());
        return new Notification();
      }))
  }

  loadProjects() {
    return this.http.get<any>('/api/v1/project')
      .pipe(map(data => {
        this.projectsSubject.next(data);
        return data;
      }))
  }

  gatherStatistics(): number[] {
    let statistics = new OverallStatistics();
    for (let project of this.projectsValue) {
      let aborted = project.aborted().length;
      let running = project.running().length;
      let failed = project.failed().length;
      statistics.aborted += aborted;
      statistics.running += running;
      statistics.passed += project.tests.length - aborted - running - failed;
      statistics.failed += failed;
    }
    return statistics.asChartData()
  }
}
