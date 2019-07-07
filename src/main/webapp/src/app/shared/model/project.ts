import {Test} from "./test";

export class Project {
  projectId: number;
  name: string;
  remotePath: string;
  localPath: string;
  tests: Test[] = []
}
