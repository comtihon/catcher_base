import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from "../model/user";
import {BehaviorSubject, Observable} from "rxjs";


@Injectable({providedIn: 'root'})
export class UserService {
  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;

  public get currentUserValue(): User {
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
  loadCurrentUser(current?: User) {
    if(!this.currentUserValue) {
      // set current user to enable jwt for interceptor
      this.currentUserSubject.next(current);
    }

    return this.http.get<any>(`/api/v1/user`)
      .subscribe(gotUser => {
        if(current) {
          gotUser.access_token = current.access_token;
          gotUser.refresh_token = current.refresh_token;
        }
        localStorage.setItem('currentUser', JSON.stringify(gotUser));
        this.currentUserSubject.next(gotUser);
        return gotUser;
      })
  }

  dropCurrentUser() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
}
