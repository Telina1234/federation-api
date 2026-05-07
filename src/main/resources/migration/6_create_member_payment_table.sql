do
$$
    begin
        if not exists(select from pg_type where typname = 'payment_mode') then
            create type payment_mode as enum (
                'CASH',
                'MOBILE_BANKING',
                'BANK_TRANSFER');
        end if;
    end
$$;

alter table if exists "collectivity_member"
    add column if not exists membership_date date default current_date;

create table if not exists "member_payment"
(
    id                          varchar primary key,
    amount                      integer,
    payment_mode                payment_mode,
    account_credited_identifier varchar,
    creation_date               date default current_date,
    member_id                   varchar references "member" (id),
    membership_fee_id           varchar references "membership_fee" (id)
);
