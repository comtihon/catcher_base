import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Project} from "../model/project";
import {map} from "rxjs/operators";
import {plainToClass} from "class-transformer";
import {Test, TestRun} from "../model/test";

@Injectable({
  providedIn: 'root'
})
export class TestService {
  constructor(private http: HttpClient) {
  }

  newTest(project: Project, test: Test) {
    return this.http.post<any>(`/api/v1/project/${project.projectId}/add_test`, test)
  }

  updateTest(test: Test) {
    return this.http.put<any>('/api/v1/test', test)
  }

  stopTest(test: Test) {
    return this.http.delete(`/api/v1/test/${test.id}`)
  }

  runTest(test: Test) {
    return this.http.post<TestRun>(`/api/v1/test/${test.id}/run`, {})
  }

  loadTest(project: Project, test: Test) {
    return this.http.get<any>(`/api/v1/test/${test.id}`)
      .pipe(map(data => {
        return plainToClass(Test, data);
      }))
  }
}
