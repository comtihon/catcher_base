import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";
import {BehaviorSubject, Observable} from "rxjs";
import {Notification} from "../model/notification";
import {OverallStatistics, Project} from "../model/project";
import {plainToClass} from "class-transformer";

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
        //let notifications = data.map(notification => plainToClass(Notification, notification));
        this.notificationsSubject.next(new Notification());
        return new Notification();
      }))
  }

  loadProjects() {
    return this.http.get<any>('/api/v1/project')
      .pipe(map(data => {
        let projects = data.map(project => plainToClass(Project, project));
        this.projectsSubject.next(projects);
        return projects;
      }))
  }

  // load project's full info
  loadProject(projectId: number) {
    return this.http.get<any>(`/api/v1/project/${projectId}`)
  }

  gatherStatistics(): number[] {
    let statistics = new OverallStatistics();
    for (let project of this.projectsValue) {
      let aborted = project.aborted().length;
      let running = project.running().length;
      let failed = project.failed().length;
      let passed = project.passed().length;
      statistics.aborted += aborted;
      statistics.running += running;
      statistics.passed += passed;
      statistics.failed += failed;
    }
    return statistics.asChartData()
  }

  newProject(project: Project) {
    return this.http.post<any>('/api/v1/project', project)
      .pipe(map(data => {
        this.loadProjects().subscribe();
        return data;
      }))
  }

}
