export class Test {
  id: number;
  name: string;
  path: string;
  data: string;
  lastRun: TestRun;
  updatedAt: string;
  runs: TestRun[];

  constructor(name: string, content: string) {
    this.name = name;
    this.data = content;
  }
}

export class TestRun {
  id: number;
  status: RunStatus;
  started: string;
  finsihed: string;
  output: string;
  test: Test
}

export enum RunStatus {
  QUEUED, STARTED, FAILED, FINISHED, ABORTED,
}
