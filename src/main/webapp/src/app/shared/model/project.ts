import {Test} from "./test";
import {RunStatus} from "./test";

export class Project {
  projectId: number;
  name: string;
  remotePath: string;
  localPath: string;
  tests: Test[] = [];

  failed(): Test[] {
    return this.tests.filter(test => {
      return test.lastRun.status == RunStatus.FAILED
    })
  }

  running(): Test[] {
    return this.tests.filter(test => {
      return test.lastRun.status == RunStatus.QUEUED || test.lastRun.status == RunStatus.STARTED
    })
  }
}
