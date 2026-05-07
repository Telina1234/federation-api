do
$$
    begin
        if not exists(select from pg_type where typname = 'transaction_type') then
            create type transaction_type as enum (
                'IN',
                'OUT');
        end if;
    end
$$;

create table if not exists "transaction"
(
    id                   varchar primary key,
    amount               numeric(12, 2),
    creation_date        date,
    transaction_type     transaction_type,
    financial_account_id varchar
);

alter table if exists "transaction"
    add column if not exists member_debited_id varchar references member ("id");