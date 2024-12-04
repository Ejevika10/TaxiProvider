TRUNCATE TABLE passenger;
ALTER SEQUENCE passenger_id_seq RESTART WITH 1;
INSERT INTO passenger (id, name, email, phone, rating, deleted) VALUES (nextval('passenger_id_seq'), 'passenger', 'passenger@mail.ru', '71234567890', '0.0', 'False');