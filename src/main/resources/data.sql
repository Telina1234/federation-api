
INSERT INTO collectivities (id, location, federation_approval)
VALUES
    ('C1', 'Antananarivo', TRUE),
    ('C2', 'Toamasina', TRUE),
    ('C3', 'Mahajanga', TRUE);

INSERT INTO members VALUES
                        ('M1', 'Jean', 'Rakoto', '1995-01-01', 'MALE', 'Tana', 'Agriculteur', 340000001, 'm1@mail.com', 'SENIOR', 'C1', TRUE, TRUE),
                        ('M2', 'Paul', 'Rabe', '1990-05-10', 'MALE', 'Tana', 'Technicien', 340000002, 'm2@mail.com', 'SENIOR', 'C1', TRUE, TRUE),
                        ('M3', 'Marie', 'Rasoanaivo', '1998-03-15', 'FEMALE', 'Tamatave', 'Agricultrice', 340000003, 'm3@mail.com', 'SENIOR', 'C2', TRUE, TRUE);


INSERT INTO referees VALUES
                         ('M3', 'M1'),
                         ('M3', 'M2');