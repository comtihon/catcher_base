import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from "../model/user";


@Injectable({providedIn: 'root'})
export class UserService {
  constructor(private http: HttpClient) {
  }

  register(user: User) {
    console.log("call register");
    if (!user.name) {
      user.name = user.email.substring(0, user.email.lastIndexOf("@"));
    }
    return this.http.post<any>(`/api/v1/user`, user);
  }

  current() {
    return this.http.get<any>(`/api/v1/user`);
  }
}
