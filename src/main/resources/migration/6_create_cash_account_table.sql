create table if not exists cash_account
(
    id              varchar primary key,
    collectivity_id varchar references collectivity (id)
);