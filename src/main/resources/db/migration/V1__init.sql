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

CREATE TABLE roles
(
  id   SERIAL PRIMARY KEY,
  name varchar(255) unique
);

CREATE TABLE users
(
  email      varchar(255) PRIMARY KEY,
  name       varchar(255) not null,
  phash      varchar(60)  not null,
  last_login TIMESTAMP,
  type       varchar(255),
  role_id    integer not null references roles(id)
);

CREATE TABLE privileges
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

CREATE TABLE teams_users
(
  users_email varchar(255) REFERENCES users (email),
  teams_name  varchar(255) REFERENCES teams (name),
  CONSTRAINT pk_teams_users PRIMARY KEY (users_email, teams_name)
);

CREATE TABLE projects_teams
(
  projects_id integer REFERENCES projects (id),
  teams_name  varchar(255) REFERENCES teams (name),
  CONSTRAINT pk_projects_teams PRIMARY KEY (projects_id, teams_name)
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
 'QUEUED',
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
  finished TIMESTAMP,
  output text
);
