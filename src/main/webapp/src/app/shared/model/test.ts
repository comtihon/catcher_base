export class Test {
  id: number;
  name: string;
  path: string;
  data: string;
  lastRun: TestRun
}

export class TestRun {
  id: number;
  status: RunStatus;
  started: string;
  finsihed: string;
  output: string
}

export enum RunStatus {
  QUEUED, STARTED, FAILED, FINISHED, ABORTED,
}
