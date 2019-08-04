import {Test} from "./test";
import {RunStatus} from "./test";
import {Team} from "./team";

export class Project {
  projectId: number;
  name: string;
  description: string;
  remotePath: string;
  localPath: string;
  tests: Test[] = [];
  teams: Team[] = [];

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

  // TODO use local counters instead of filtering every time
  passed(): Test[] {
    return this.tests.filter(test => {
      return test.lastRun.status == RunStatus.FINISHED
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
