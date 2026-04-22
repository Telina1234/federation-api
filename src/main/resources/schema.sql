DROP TABLE IF EXISTS referees;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS collectivities;

CREATE TABLE collectivities (
                                id VARCHAR(50) PRIMARY KEY,
                                number VARCHAR(50) UNIQUE,
                                name VARCHAR(100) UNIQUE,
                                location VARCHAR(100),
                                federation_approval BOOLEAN DEFAULT FALSE
);

CREATE TABLE members (
                         id VARCHAR(50) PRIMARY KEY,
                         first_name VARCHAR(50),
                         last_name VARCHAR(50),
                         birth_date DATE,
                         gender VARCHAR(10),
                         address VARCHAR(100),
                         profession VARCHAR(50),
                         phone_number BIGINT,
                         email VARCHAR(100),
                         occupation VARCHAR(50),
                         collectivity_id VARCHAR(50),
                         registration_fee_paid BOOLEAN,
                         membership_dues_paid BOOLEAN,
                         FOREIGN KEY (collectivity_id) REFERENCES collectivities(id)
);

CREATE TABLE referees (
                          member_id VARCHAR(50),
                          referee_id VARCHAR(50),
                          PRIMARY KEY (member_id, referee_id),
                          FOREIGN KEY (member_id) REFERENCES members(id),
                          FOREIGN KEY (referee_id) REFERENCES members(id)
);