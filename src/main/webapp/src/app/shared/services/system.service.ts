import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";
import {BehaviorSubject, Observable} from "rxjs";
import {SystemInfo} from "../model/systemInfo";

@Injectable({
  providedIn: 'root'
})
export class SystemService {

  private systemInfoSubject: BehaviorSubject<SystemInfo>;
  public systemInfo: Observable<SystemInfo>;

  constructor(private _router: Router, private http: HttpClient) {
    this.systemInfoSubject = new BehaviorSubject<SystemInfo>(JSON.parse(localStorage.getItem('systemInfo')));
    this.systemInfo = this.systemInfoSubject.asObservable();
  }

  public get systemInfoValue(): SystemInfo {
    return this.systemInfoSubject.value;
  }

  loadSystemInfo() {
    return this.http.get<any>('/api/v1/system')
      .pipe(map(data => {
        localStorage.setItem('systemInfo', JSON.stringify(data));
        this.systemInfoSubject.next(data);
        return data;
      }))
  }
}
