insert into projects
values (1, 'test1', './proj1', null),
       (2, 'test2', './proj2', null);

insert into teams
values ('teamA'),
       ('teamB');

-- both passwords are 'test'
insert into users
values ('userA@test.de', 'A', '$2a$11$JO4sHHNC8LY7S8Ua87w1geeYBOuNB.K9rhg67xNz9P0ThaFbRNtKC', null, 'user', 2),
       ('userB@test.de', 'B', '$2a$11$JO4sHHNC8LY7S8Ua87w1geeYBOuNB.K9rhg67xNz9P0ThaFbRNtKC', null, 'user', 2);

insert into teams_users
values ('userA@test.de', 'teamA'),
       ('userB@test.de', 'teamB');

insert into projects_teams
values (1, 'teamA'),
       (2, 'teamB');

insert into tests
values (1, 'test1_1', './proj1/tests/test1', 1),
       (2, 'test1_2', './proj1/tests/test2', 1),
       (3, 'test2_1', './proj2/tests/test1', 2),
       (4, 'test2_2', './proj2/tests/test2', 2);

insert into runs
values (1, 1, 'FAILED', '2019-07-18 10:10:25-07', '2019-07-18 10:12:25-07', 'error'),
       (2, 1, 'FAILED', '2019-07-18 11:10:25-07', '2019-07-18 11:12:25-07', 'error'),
       (3, 1, 'STARTED', '2019-07-18 12:00:00-07', null, 'ok'),
       (4, 3, 'FINISHED', '2019-07-18 12:00:00-07', '2019-07-18 12:03:25-07', 'ok'),
       (5, 4, 'FINISHED', '2019-07-18 12:00:00-07', '2019-07-18 12:03:25-07', 'ok');