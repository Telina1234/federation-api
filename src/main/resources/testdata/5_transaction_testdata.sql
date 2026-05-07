-- ============================================================
-- TRANSACTIONS — COLLECTIVITÉ 1  (col-1)
-- Toutes vers le compte cash C1-A-CASH
-- ============================================================
INSERT INTO "transaction" (id, amount, creation_date, transaction_type, financial_account_id, member_debited_id)
VALUES ('tx-col1-C1M1', 100000.00, '2026-01-01', 'IN', 'C1-A-CASH', 'C1-M1'),
       ('tx-col1-C1M2', 100000.00, '2026-01-01', 'IN', 'C1-A-CASH', 'C1-M2'),
       ('tx-col1-C1M3', 100000.00, '2026-01-01', 'IN', 'C1-A-CASH', 'C1-M3'),
       ('tx-col1-C1M4', 100000.00, '2026-01-01', 'IN', 'C1-A-CASH', 'C1-M4'),
       ('tx-col1-C1M5', 100000.00, '2026-01-01', 'IN', 'C1-A-CASH', 'C1-M5'),
       ('tx-col1-C1M6', 100000.00, '2026-01-01', 'IN', 'C1-A-CASH', 'C1-M6'),
       ('tx-col1-C1M7', 60000.00, '2026-01-01', 'IN', 'C1-A-CASH', 'C1-M7'),
       ('tx-col1-C1M8', 90000.00, '2026-01-01', 'IN', 'C1-A-CASH', 'C1-M8');

-- ============================================================
-- TRANSACTIONS — COLLECTIVITÉ 2  (col-2)
-- C1-M1..C1-M6 → cash (C2-A-CASH)
-- C1-M7..C1-M8 → mobile money (C2-A-MOBILE-1)
-- ============================================================
INSERT INTO "transaction" (id, amount, creation_date, transaction_type, financial_account_id, member_debited_id)
VALUES ('tx-col2-C1M1', 60000.00, '2026-01-01', 'IN', 'C2-A-CASH', 'C1-M1'),
       ('tx-col2-C1M2', 90000.00, '2026-01-01', 'IN', 'C2-A-CASH', 'C1-M2'),
       ('tx-col2-C1M3', 100000.00, '2026-01-01', 'IN', 'C2-A-CASH', 'C1-M3'),
       ('tx-col2-C1M4', 100000.00, '2026-01-01', 'IN', 'C2-A-CASH', 'C1-M4'),
       ('tx-col2-C1M5', 100000.00, '2026-01-01', 'IN', 'C2-A-CASH', 'C1-M5'),
       ('tx-col2-C1M6', 100000.00, '2026-01-01', 'IN', 'C2-A-CASH', 'C1-M6'),
       ('tx-col2-C1M7', 40000.00, '2026-01-01', 'IN', 'C2-A-MOBILE-1', 'C1-M7'),
       ('tx-col2-C1M8', 60000.00, '2026-01-01', 'IN', 'C2-A-MOBILE-1', 'C1-M8');