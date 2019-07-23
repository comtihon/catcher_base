import {Test} from "./test";
import {RunStatus} from "./test";

export class Project {
  projectId: number;
  name: string;
  remotePath: string;
  localPath: string;
  tests: Test[] = [];

  // TODO iterate tests only once
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

  aborted(): Test[] {
    return this.tests.filter(test => {
      return test.lastRun.status == RunStatus.ABORTED
    })
  }
}

export class OverallStatistics {
  passed: number = 0;
  failed: number = 0;
  running: number = 0;
  aborted: number = 0;

  asChartData(): number[] {
    return [this.passed, this.failed, this.running, this.aborted]
  }
}
