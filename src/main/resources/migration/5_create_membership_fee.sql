do
$$
    begin
        if not exists(select from pg_type where typname = 'frequency') then
            create type frequency as enum (
                'WEEKLY',
                'MONTHLY',
                'ANNUALLY',
                'PUNCTUALLY');
        end if;
    end
$$;

do
$$
    begin
        if not exists(select from pg_type where typname = 'activity_status') then
            create type activity_status as enum (
                'ACTIVE',
                'INACTIVE');
        end if;
    end
$$;

create table if not exists "membership_fee"
(
    id              varchar primary key,
    label           varchar,
    amount          numeric(12, 2),
    eligible_from   date,
    status          activity_status,
    frequency       frequency,
    collectivity_id varchar references "collectivity" (id)
);