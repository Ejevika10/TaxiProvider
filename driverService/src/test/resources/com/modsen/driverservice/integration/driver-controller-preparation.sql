ALTER SEQUENCE driver_id_seq RESTART WITH 1
TRUNCATE TABLE driver CASCADE
INSERT INTO driver (id, name, email, phone, rating, deleted) VALUES (nextval('driver_id_seq'), 'Driver', 'driver@email.com', '71234567890', '0.0', 'False')