import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {AppSettings} from '../appsettings';
import {catchError, map} from "rxjs/operators";
import {UserService} from "./user.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private _router: Router, private http: HttpClient, private userService: UserService) {
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
        this.userService.loadCurrentUser(user);
        return user;
      }))
  }

  logout() {
    this.userService.dropCurrentUser();
  }
}
