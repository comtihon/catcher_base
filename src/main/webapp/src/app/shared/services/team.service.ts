import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import {Team} from "../model/team";

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor(private http: HttpClient) {
    this.teamsSubject = new BehaviorSubject<Team[]>([]);
    this.teams = this.teamsSubject.asObservable();
  }

  private teamsSubject: BehaviorSubject<Team[]>;
  public teams: Observable<Team[]>;

  public get teamsValue(): Team[] {
    return this.teamsSubject.value;
  }

  loadTeams() {
    return this.http.get<any>(`/api/v1/team`)
      .subscribe(teams => {
        this.teamsSubject.next(teams);
        return teams;
      }) // TODO error?
  }
}
