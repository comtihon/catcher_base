import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {UserService} from "../services/user.service";

@Injectable({
  providedIn: 'root'
})
export class JwtInterceptor implements HttpInterceptor {

  constructor(public userService: UserService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    let currentUser = this.userService.currentUserValue;
    if (currentUser && currentUser.access_token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${currentUser.access_token}`
        }
      });
    }

    return next.handle(request);
  }
}
