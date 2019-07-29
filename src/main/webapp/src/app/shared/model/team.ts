import {User} from "./user";

export class Team {
  name: string;
  users: User[] = [];

  constructor(name: string) {
    this.name = name;
  }
}
