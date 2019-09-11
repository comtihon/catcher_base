import {Test} from "./test";
import {Team} from "./team";
import {Type} from "class-transformer";

export class Project {
  projectId: number;
  name: string;
  description: string;
  remotePath: string;
  localPath: string;
  @Type(() => Test)
  tests: Test[];
  @Type(() => Team)
  teams: Team[];

  // TODO iterate tests only once
  failed(): Test[] {
    return this.tests.filter(test => {
      return test.lastRun && test.lastRun.status == 'FAILED'
    })
  }

  running(): Test[] {
    return this.tests.filter(test => {
      return test.lastRun &&  (test.lastRun.status == 'QUEUED' || test.lastRun.status == 'STARTED')
    })
  }

  aborted(): Test[] {
    return this.tests.filter(test => {
      return test.lastRun && test.lastRun.status == 'ABORTED'
    })
  }

  // TODO use local counters instead of filtering every time
  passed(): Test[] {
    return this.tests.filter(test => {
      return test.lastRun && test.lastRun.status == 'FINISHED'
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
