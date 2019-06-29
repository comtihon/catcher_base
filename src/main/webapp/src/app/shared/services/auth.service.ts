import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {AppSettings} from '../appsettings';
import {map} from "rxjs/operators";
import {User} from "../model/user";
import {BehaviorSubject, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;


  constructor(private _router: Router, private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User {
    return this.currentUserSubject.value;
  }

  login(email: string, password: string) {
    let headers = {
      'Authorization': 'Basic ' + btoa(`${AppSettings.oauthLogin}:${AppSettings.oauthPass}`),
      'Content-Type': 'application/x-www-form-urlencoded'
    };
    return this.http.post<any>('/oauth/token',
      `username=${email}&password=${password}&grant_type=password`,
      {headers: headers})
      .pipe(map(user => {
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
        return user;
      }))
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
}
