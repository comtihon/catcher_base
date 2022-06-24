import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from "../model/user";
import {BehaviorSubject, Observable} from "rxjs";
import {plainToClass} from "class-transformer";
import {Project} from "../model/project";


@Injectable({providedIn: 'root'})
export class UserService {
  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User | null>(plainToClass(User, JSON.parse(localStorage.getItem('currentUser') || '{}' )));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  register(user: User) {
    if (!user.name) {
      user.name = user.email.substring(0, user.email.lastIndexOf("@"));
    }
    return this.http.post<any>(`/api/v1/user`, user);
  }

  // current is optional user, returned from login (has only token information)
  // this function will fill the rest
  loadCurrentUser(current?: any) {
    if (!this.currentUserValue) {
      // set current user to enable jwt for interceptor
      this.currentUserSubject.next(plainToClass(User, current));
    }

    return this.http.get<any>(`/api/v1/user`)
      .subscribe(gotUser => {
        if (current) {
          gotUser.access_token = current.access_token;
          gotUser.refresh_token = current.refresh_token;
        }
        let user = plainToClass(User, gotUser);
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
        return user;
      })
  }

  dropCurrentUser() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
}
