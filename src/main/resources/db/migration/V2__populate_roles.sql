insert into privileges(id, name)
values
  (1, 'launch_tests'),
  (2, 'modify_tests'),
  (3, 'modify_steps'),
  (4, 'edit_templates'),
  (5, 'modify_teams'),
  (6, 'modify_projects');

insert into roles(id, name)
values
  (1, 'admin'),
  (2, 'user');

insert into roles_privileges(role_id, privilege_id)
values
  (1, 1),
  (1, 2),
  (1, 3),
  (1, 4),
  (1, 5),
  (1, 6),
  (2, 1),
  (2, 2),
  (2, 3);