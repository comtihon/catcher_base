export class Test {
  id: number;
  name: string;
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
  status: string;  // TODO should be enum
  queued: string;
  started: string;
  finsihed: string;
  output: string;
  test: Test
}
