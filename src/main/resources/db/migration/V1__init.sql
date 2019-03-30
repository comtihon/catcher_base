CREATE TABLE projects
(
  id          SERIAL PRIMARY KEY,
  name        varchar(255) unique not null,
  local_path  varchar(500)        not null,
  remote_path varchar(500)
);

CREATE TABLE teams
(
  name varchar(255) PRIMARY KEY
);

CREATE TABLE users
(
  email      varchar(255) PRIMARY KEY,
  name       varchar(255) not null,
  phash      varchar(60)  not null,
  last_login TIMESTAMP,
  type       varchar(255)
);

CREATE TABLE privileges
(
  id   SERIAL PRIMARY KEY,
  name varchar(255) unique
);

CREATE TABLE roles
(
  id   SERIAL PRIMARY KEY,
  name varchar(255) unique
);

CREATE TABLE roles_privileges
(
  role_id      integer REFERENCES roles (id),
  privilege_id integer REFERENCES privileges (id),
  CONSTRAINT pk_roles_privileges PRIMARY KEY (role_id, privilege_id)
);

CREATE TABLE users_roles
(
  user_email varchar(255) REFERENCES users (email),
  role_id    integer REFERENCES roles (id),
  CONSTRAINT pk_users_roles PRIMARY KEY (user_email, role_id)
);

CREATE TABLE users_teams
(
  user_email varchar(255) REFERENCES users (email),
  team_name  varchar(255) REFERENCES teams (name),
  CONSTRAINT pk_users_teams PRIMARY KEY (user_email, team_name)
);

CREATE TABLE projects_teams
(
  project_id integer REFERENCES projects (id),
  team_name  varchar(255) REFERENCES teams (name),
  CONSTRAINT pk_projects_teams PRIMARY KEY (project_id, team_name)
);

CREATE TABLE tests
(
  id   SERIAL PRIMARY KEY,
  name varchar(255),
  path varchar(500) unique,
  project_id integer REFERENCES projects (id)
);

CREATE TABLE projects_tests
(
  project_id integer REFERENCES projects (id),
  tests_id  integer REFERENCES tests (id),
  CONSTRAINT pk_projects_tests PRIMARY KEY (project_id, tests_id)
);

CREATE TYPE run_status AS ENUM
(
 'STARTED',
 'FAILED',
 'FINISHED',
 'ABORTED'
);

CREATE TABLE runs
(
  id SERIAL PRIMARY KEY,
  test_id integer REFERENCES tests (id),
  status run_status not null,
  started TIMESTAMP not null,
  finished TIMESTAMP
);
