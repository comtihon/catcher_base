import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import {Team} from "../model/team";

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  constructor(private http: HttpClient) {
    this.rolesSubject = new BehaviorSubject<Team[]>([]);
    this.roles = this.rolesSubject.asObservable();
  }

  private rolesSubject: BehaviorSubject<Team[]>;
  public roles: Observable<Team[]>;

  public get rolesValue(): Team[] {
    return this.rolesSubject.value;
  }

  loadRoles() {
    return this.http.get<any>(`/api/v1/team`)
      .subscribe(teams => {
        this.rolesSubject.next(teams);
        return teams;
      }) // TODO error?
  }
}
