ALTER SEQUENCE car_id_seq RESTART WITH 1;
TRUNCATE TABLE car CASCADE;
INSERT INTO car (id, color, model, brand, number, deleted, driver_id) VALUES (nextval('car_id_seq'), 'red', 'sedan', 'audi', '12345', 'False', '00000000-0000-0001-0000-000000000001')