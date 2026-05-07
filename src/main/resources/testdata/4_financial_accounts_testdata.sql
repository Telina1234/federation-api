-- ============================================================
-- COMPTES CASH  (table cash_account)
-- ============================================================
INSERT INTO "cash_account" (id, collectivity_id)
VALUES ('C1-A-CASH', 'col-1'),
       ('C2-A-CASH', 'col-2'),
       ('C3-A-CASH', 'col-3');

-- ============================================================
-- COMPTES MOBILE MONEY  (table mobile_banking_account)
-- ============================================================
-- col-3 n'a pas de compte mobile dans les données fournies
INSERT INTO "mobile_banking_account" (id, holder_name, service, mobile_number, collectivity_id)
VALUES ('C1-A-MOBILE-1', 'Mpanorina', 'ORANGE_MONEY', '0370489612', 'col-1'),
       ('C2-A-MOBILE-1', 'Dobo voalohany', 'ORANGE_MONEY', '0320489612', 'col-2');