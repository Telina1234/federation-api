do
$$
    begin
        if not exists(select from pg_type where typname = 'payment_mode') then
            create type payment_mode as enum (
                'BANK_TRANSFER',
                'MOBILE_BANKING',
                'CASH');
        end if;
    end
$$;

create table if not exists "member_payment"
(
    id                   varchar primary key,
    amount               numeric(12, 2),
    creation_date        date,
    member_debited_id    varchar references member ("id"),
    membership_fee_id    varchar references membership_fee ("id"),
    payment_mode         payment_mode,
    financial_account_id varchar
);

do
$$
    begin
        if exists(select
                  from information_schema.columns
                  where table_name = 'member_payment'
                    and column_name = 'member_id')
            and not exists(select
                           from information_schema.columns
                           where table_name = 'member_payment'
                             and column_name = 'member_debited_id') then
            alter table "member_payment"
                rename column member_id to member_debited_id;
        end if;
    end
$$;

do
$$
    begin
        if exists(select
                  from information_schema.columns
                  where table_name = 'member_payment'
                    and column_name = 'account_credited_identifier')
            and not exists(select
                           from information_schema.columns
                           where table_name = 'member_payment'
                             and column_name = 'financial_account_id') then
            alter table "member_payment"
                rename column account_credited_identifier to financial_account_id;
        end if;
    end
$$;

alter table if exists "member_payment"
    add column if not exists member_debited_id varchar references member ("id"),
    add column if not exists financial_account_id varchar;
