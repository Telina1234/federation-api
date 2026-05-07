-- Final evaluation data for May 2026.
-- Run after migrations 1..11 and base testdata 1..6.

delete from member_payment;
delete from "transaction";
delete from membership_fee;

-- Keep the original three collectivities and their base members, but reset their membership date.
update collectivity_member
set membership_date = '2026-01-01'
where collectivity_id in ('col-1', 'col-2', 'col-3');

-- Financial accounts added for collectivity 3.
insert into bank_account (id, holder_name, bank_name, bank_code, branch_code, account_number, key, collectivity_id)
values ('C3-A-BANK-1', 'Koto', 'BMOI', 4, 1, 1234567890, 12, 'col-3'),
       ('C3-A-BANK-2', 'Naivo', 'BRED', 8, 3, 4567890123, 58, 'col-3')
on conflict (id) do update set holder_name = excluded.holder_name,
                              bank_name = excluded.bank_name,
                              bank_code = excluded.bank_code,
                              branch_code = excluded.branch_code,
                              account_number = excluded.account_number,
                              key = excluded.key,
                              collectivity_id = excluded.collectivity_id;

insert into mobile_banking_account (id, holder_name, service, mobile_number, collectivity_id)
values ('C3-A-MOBILE-1', 'Kolo', 'MVOLA', '0341889612', 'col-3')
on conflict (id) do update set holder_name = excluded.holder_name,
                              service = excluded.service,
                              mobile_number = excluded.mobile_number,
                              collectivity_id = excluded.collectivity_id;

-- Final membership fees.
insert into membership_fee (id, label, amount, eligible_from, status, frequency, collectivity_id)
values ('cot-1', 'Cotisation annuelle', 200000, '2026-01-01', 'ACTIVE', 'ANNUALLY', 'col-1'),
       ('cot-2', 'Famangiana', 20000, '2026-04-30', 'ACTIVE', 'PUNCTUALLY', 'col-1'),
       ('cot-3', 'Cotisation annuelle', 200000, '2026-01-01', 'ACTIVE', 'ANNUALLY', 'col-2'),
       ('cot-4', 'Cotisation 2025', 100000, '2025-01-01', 'INACTIVE', 'ANNUALLY', 'col-2'),
       ('cot-5', 'Cotisation mensuelle', 25000, '2026-04-01', 'ACTIVE', 'MONTHLY', 'col-3')
on conflict (id) do update set label = excluded.label,
                              amount = excluded.amount,
                              eligible_from = excluded.eligible_from,
                              status = excluded.status,
                              frequency = excluded.frequency,
                              collectivity_id = excluded.collectivity_id;

-- Final payments. Payment modes from the PDF are mapped to the project enum:
-- MOBILE_MONEY/MOBILE MONEY -> MOBILE_BANKING, BANK -> BANK_TRANSFER.
insert into member_payment (id, amount, creation_date, member_debited_id, membership_fee_id, payment_mode, financial_account_id)
values
       -- Collectivity 1
       ('pay-col1-C1M1-20260101', 200000, '2026-01-01', 'C1-M1', 'cot-1', 'CASH', 'C1-A-CASH'),
       ('pay-col1-C1M2-20260101', 200000, '2026-01-01', 'C1-M2', 'cot-1', 'CASH', 'C1-A-CASH'),
       ('pay-col1-C1M3-20260101', 200000, '2026-01-01', 'C1-M3', 'cot-1', 'MOBILE_BANKING', 'C1-A-MOBILE-1'),
       ('pay-col1-C1M4-20260101', 200000, '2026-01-01', 'C1-M4', 'cot-1', 'MOBILE_BANKING', 'C1-A-MOBILE-1'),
       ('pay-col1-C1M5-20260101', 150000, '2026-01-01', 'C1-M5', 'cot-1', 'MOBILE_BANKING', 'C1-A-MOBILE-1'),
       ('pay-col1-C1M6-20260501', 100000, '2026-05-01', 'C1-M6', 'cot-1', 'CASH', 'C1-A-CASH'),
       ('pay-col1-C1M7-20260501', 60000, '2026-05-01', 'C1-M7', 'cot-1', 'CASH', 'C1-A-CASH'),
       ('pay-col1-C1M8-20260501', 90000, '2026-05-01', 'C1-M8', 'cot-1', 'CASH', 'C1-A-CASH'),
       -- Collectivity 2
       ('pay-col2-C1M1-20260101', 120000, '2026-01-01', 'C1-M1', 'cot-3', 'CASH', 'C2-A-CASH'),
       ('pay-col2-C1M2-20260101', 180000, '2026-01-01', 'C1-M2', 'cot-3', 'CASH', 'C2-A-CASH'),
       ('pay-col2-C1M3-20260101', 200000, '2026-01-01', 'C1-M3', 'cot-3', 'CASH', 'C2-A-CASH'),
       ('pay-col2-C1M4-20260101', 200000, '2026-01-01', 'C1-M4', 'cot-3', 'CASH', 'C2-A-CASH'),
       ('pay-col2-C1M5-20260101', 200000, '2026-01-01', 'C1-M5', 'cot-3', 'CASH', 'C2-A-CASH'),
       ('pay-col2-C1M6-20260101', 200000, '2026-01-01', 'C1-M6', 'cot-3', 'CASH', 'C2-A-CASH'),
       ('pay-col2-C1M7-20260101', 80000, '2026-01-01', 'C1-M7', 'cot-3', 'MOBILE_BANKING', 'C2-A-MOBILE-1'),
       ('pay-col2-C1M8-20260101', 120000, '2026-01-01', 'C1-M8', 'cot-3', 'MOBILE_BANKING', 'C2-A-MOBILE-1'),
       -- Collectivity 3 - April
       ('pay-col3-C3M1-20260401', 25000, '2026-04-01', 'C3-M1', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-1'),
       ('pay-col3-C3M2-20260401', 25000, '2026-04-01', 'C3-M2', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-1'),
       ('pay-col3-C3M3-20260401', 25000, '2026-04-01', 'C3-M3', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-1'),
       ('pay-col3-C3M4-20260401', 25000, '2026-04-01', 'C3-M4', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-1'),
       ('pay-col3-C3M5-20260401', 25000, '2026-04-01', 'C3-M5', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-2'),
       ('pay-col3-C3M6-20260401', 25000, '2026-04-01', 'C3-M6', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-2'),
       ('pay-col3-C3M7-20260401', 25000, '2026-04-01', 'C3-M7', 'cot-5', 'CASH', 'C3-A-CASH'),
       ('pay-col3-C3M8-20260401', 25000, '2026-04-01', 'C3-M8', 'cot-5', 'CASH', 'C3-A-CASH'),
       -- Collectivity 3 - May
       ('pay-col3-C3M1-20260501', 25000, '2026-05-01', 'C3-M1', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-1'),
       ('pay-col3-C3M2-20260501', 25000, '2026-05-01', 'C3-M2', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-1'),
       ('pay-col3-C3M3-20260501', 15000, '2026-05-01', 'C3-M3', 'cot-5', 'BANK_TRANSFER', 'C3-A-MOBILE-1'),
       ('pay-col3-C3M4-20260501', 15000, '2026-05-01', 'C3-M4', 'cot-5', 'BANK_TRANSFER', 'C3-A-MOBILE-1'),
       ('pay-col3-C3M5-20260501', 20000, '2026-05-01', 'C3-M5', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-2'),
       ('pay-col3-C3M6-20260501', 25000, '2026-05-01', 'C3-M6', 'cot-5', 'BANK_TRANSFER', 'C3-A-BANK-2'),
       ('pay-col3-C3M7-20260501', 5000, '2026-05-01', 'C3-M7', 'cot-5', 'CASH', 'C3-A-CASH'),
       ('pay-col3-C3M8-20260501', 5000, '2026-05-01', 'C3-M8', 'cot-5', 'CASH', 'C3-A-CASH')
on conflict (id) do update set amount = excluded.amount,
                              creation_date = excluded.creation_date,
                              member_debited_id = excluded.member_debited_id,
                              membership_fee_id = excluded.membership_fee_id,
                              payment_mode = excluded.payment_mode,
                              financial_account_id = excluded.financial_account_id;

insert into "transaction" (id, amount, creation_date, transaction_type, financial_account_id, member_debited_id)
select replace(id, 'pay-', 'tx-'), amount, creation_date, 'IN', financial_account_id, member_debited_id
from member_payment
where id like 'pay-col%'
on conflict (id) do update set amount = excluded.amount,
                              creation_date = excluded.creation_date,
                              transaction_type = excluded.transaction_type,
                              financial_account_id = excluded.financial_account_id,
                              member_debited_id = excluded.member_debited_id;

-- New members and their collectivity membership dates.
insert into "member" (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, registration_fee_paid, membership_dues_paid)
values ('C1-M9', 'Final C1 9', 'Member', '2000-01-09', 'MALE', 'Adresse C1-9', 'Agriculteur', '0340000009', 'c1.m9@fed-agri.mg', 'JUNIOR', true, true),
       ('C1-M10', 'Final C1 10', 'Member', '2000-01-10', 'FEMALE', 'Adresse C1-10', 'Agriculteur', '0340000010', 'c1.m10@fed-agri.mg', 'JUNIOR', true, true),
       ('C1-M11', 'Final C1 11', 'Member', '2000-01-11', 'MALE', 'Adresse C1-11', 'Agriculteur', '0340000011', 'c1.m11@fed-agri.mg', 'JUNIOR', true, true),
       ('C1-M12', 'Final C1 12', 'Member', '2000-01-12', 'FEMALE', 'Adresse C1-12', 'Agriculteur', '0340000012', 'c1.m12@fed-agri.mg', 'JUNIOR', true, true),
       ('C2-M9', 'Final C2 9', 'Member', '2000-02-09', 'MALE', 'Adresse C2-9', 'Agriculteur', '0320000009', 'c2.m9@fed-agri.mg', 'JUNIOR', true, true),
       ('C2-M10', 'Final C2 10', 'Member', '2000-02-10', 'FEMALE', 'Adresse C2-10', 'Agriculteur', '0320000010', 'c2.m10@fed-agri.mg', 'JUNIOR', true, true),
       ('C2-M11', 'Final C2 11', 'Member', '2000-02-11', 'MALE', 'Adresse C2-11', 'Agriculteur', '0320000011', 'c2.m11@fed-agri.mg', 'JUNIOR', true, true),
       ('C3-M9', 'Final C3 9', 'Member', '2000-03-09', 'MALE', 'Adresse C3-9', 'Agriculteur', '0330000009', 'c3.m9@fed-agri.mg', 'JUNIOR', true, true),
       ('C3-M10', 'Final C3 10', 'Member', '2000-03-10', 'FEMALE', 'Adresse C3-10', 'Agriculteur', '0330000010', 'c3.m10@fed-agri.mg', 'JUNIOR', true, true),
       ('C3-M11', 'Final C3 11', 'Member', '2000-03-11', 'MALE', 'Adresse C3-11', 'Agriculteur', '0330000011', 'c3.m11@fed-agri.mg', 'JUNIOR', true, true),
       ('C3-M12', 'Final C3 12', 'Member', '2000-03-12', 'FEMALE', 'Adresse C3-12', 'Agriculteur', '0330000012', 'c3.m12@fed-agri.mg', 'JUNIOR', true, true),
       ('C3-M13', 'Final C3 13', 'Member', '2000-03-13', 'MALE', 'Adresse C3-13', 'Agriculteur', '0330000013', 'c3.m13@fed-agri.mg', 'JUNIOR', true, true),
       ('C3-M14', 'Final C3 14', 'Member', '2000-03-14', 'FEMALE', 'Adresse C3-14', 'Agriculteur', '0330000014', 'c3.m14@fed-agri.mg', 'JUNIOR', true, true)
on conflict (id) do update set first_name = excluded.first_name,
                              last_name = excluded.last_name,
                              birth_date = excluded.birth_date,
                              gender = excluded.gender,
                              address = excluded.address,
                              profession = excluded.profession,
                              phone_number = excluded.phone_number,
                              email = excluded.email,
                              occupation = excluded.occupation,
                              registration_fee_paid = excluded.registration_fee_paid,
                              membership_dues_paid = excluded.membership_dues_paid;

insert into collectivity_member (id, member_id, collectivity_id, membership_date)
values ('cm-col1-C1M9', 'C1-M9', 'col-1', '2026-04-01'),
       ('cm-col1-C1M10', 'C1-M10', 'col-1', '2026-04-01'),
       ('cm-col1-C1M11', 'C1-M11', 'col-1', '2026-05-01'),
       ('cm-col1-C1M12', 'C1-M12', 'col-1', '2026-06-01'),
       ('cm-col2-C2M9', 'C2-M9', 'col-2', '2026-03-01'),
       ('cm-col2-C2M10', 'C2-M10', 'col-2', '2026-03-01'),
       ('cm-col2-C2M11', 'C2-M11', 'col-2', '2026-03-01'),
       ('cm-col3-C3M9', 'C3-M9', 'col-3', '2026-01-01'),
       ('cm-col3-C3M10', 'C3-M10', 'col-3', '2026-02-01'),
       ('cm-col3-C3M11', 'C3-M11', 'col-3', '2026-02-01'),
       ('cm-col3-C3M12', 'C3-M12', 'col-3', '2026-03-01'),
       ('cm-col3-C3M13', 'C3-M13', 'col-3', '2026-03-01'),
       ('cm-col3-C3M14', 'C3-M14', 'col-3', '2026-03-01')
on conflict (id) do update set member_id = excluded.member_id,
                              collectivity_id = excluded.collectivity_id,
                              membership_date = excluded.membership_date;

insert into member_referee (id, member_refereed_id, member_referee_id)
values ('mr-C1M9-C1M1', 'C1-M9', 'C1-M1'),
       ('mr-C1M9-C1M2', 'C1-M9', 'C1-M2'),
       ('mr-C1M10-C1M1', 'C1-M10', 'C1-M1'),
       ('mr-C1M10-C1M2', 'C1-M10', 'C1-M2'),
       ('mr-C1M11-C1M1', 'C1-M11', 'C1-M1'),
       ('mr-C1M11-C1M2', 'C1-M11', 'C1-M2'),
       ('mr-C1M12-C1M1', 'C1-M12', 'C1-M1'),
       ('mr-C1M12-C1M2', 'C1-M12', 'C1-M2'),
       ('mr-C2M9-C1M1', 'C2-M9', 'C1-M1'),
       ('mr-C2M9-C1M2', 'C2-M9', 'C1-M2'),
       ('mr-C2M10-C1M1', 'C2-M10', 'C1-M1'),
       ('mr-C2M10-C1M2', 'C2-M10', 'C1-M2'),
       ('mr-C2M11-C1M1', 'C2-M11', 'C1-M1'),
       ('mr-C2M11-C1M2', 'C2-M11', 'C1-M2'),
       ('mr-C3M9-C3M1', 'C3-M9', 'C3-M1'),
       ('mr-C3M9-C3M2', 'C3-M9', 'C3-M2'),
       ('mr-C3M10-C3M1', 'C3-M10', 'C3-M1'),
       ('mr-C3M10-C3M2', 'C3-M10', 'C3-M2'),
       ('mr-C3M11-C3M1', 'C3-M11', 'C3-M1'),
       ('mr-C3M11-C3M2', 'C3-M11', 'C3-M2'),
       ('mr-C3M12-C3M1', 'C3-M12', 'C3-M1'),
       ('mr-C3M12-C3M2', 'C3-M12', 'C3-M2'),
       ('mr-C3M13-C3M1', 'C3-M13', 'C3-M1'),
       ('mr-C3M13-C3M2', 'C3-M13', 'C3-M2'),
       ('mr-C3M14-C3M1', 'C3-M14', 'C3-M1'),
       ('mr-C3M14-C3M2', 'C3-M14', 'C3-M2')
on conflict (id) do nothing;
