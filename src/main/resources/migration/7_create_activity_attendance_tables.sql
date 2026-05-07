do
$$
    begin
        if not exists(select from pg_type where typname = 'attendance_status') then
            create type attendance_status as enum (
                'PRESENT',
                'ABSENT');
        end if;
    end
$$;

create table if not exists "collectivity_activity"
(
    id                varchar primary key,
    label             varchar,
    description       varchar,
    activity_date     date,
    mandatory         boolean,
    target_occupation member_occupation,
    collectivity_id   varchar references "collectivity" (id)
);

create table if not exists "activity_attendance"
(
    id          varchar primary key,
    activity_id varchar references "collectivity_activity" (id),
    member_id   varchar references "member" (id),
    status      attendance_status,
    unique (activity_id, member_id)
);
