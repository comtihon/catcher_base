export class User {
  id: number;
  name: string;
  email: string;
  password: string;
  role: Role;
  access_token: string;
  refresh_token: string;

  isAdmin(): boolean {
    return this.role.name == 'admin'
  }
}

export class Role {
  name: String;
  privileges: String[]; // TODO enum?
}
