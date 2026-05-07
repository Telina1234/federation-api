do
$$
    begin
        if not exists(select from pg_type where typname = 'mobile_banking_service') then
            create type mobile_banking_service as enum (
                'ORANGE_MONEY',
                'MVOLA',
                'AIRTEL_MONEY');
        end if;
    end
$$;

create table if not exists "mobile_banking_account"
(
    id              varchar primary key,
    holder_name     varchar,
    service         mobile_banking_service,
    mobile_number   varchar,
    collectivity_id varchar references "collectivity" (id)
);