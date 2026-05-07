insert into collectivity_activity (id, label, description, activity_date, mandatory, target_occupation, collectivity_id)
values ('act-col1-202601-ag', 'Assemblée générale mensuelle', 'Assemblée générale de janvier', '2026-01-12', true, null, 'col-1'),
       ('act-col1-202601-junior', 'Formation obligatoire junior', 'Formation des membres juniors', '2026-01-24', true, 'JUNIOR', 'col-1')
on conflict (id) do nothing;

insert into activity_attendance (id, activity_id, member_id, status)
values ('att-act-col1-ag-c1m1', 'act-col1-202601-ag', 'C1-M1', 'PRESENT'),
       ('att-act-col1-ag-c1m2', 'act-col1-202601-ag', 'C1-M2', 'PRESENT'),
       ('att-act-col1-ag-c1m3', 'act-col1-202601-ag', 'C1-M3', 'PRESENT'),
       ('att-act-col1-ag-c1m4', 'act-col1-202601-ag', 'C1-M4', 'ABSENT'),
       ('att-act-col1-ag-c1m5', 'act-col1-202601-ag', 'C1-M5', 'PRESENT')
on conflict (id) do nothing;
