ALTER SEQUENCE ride_id_seq RESTART WITH 1;
TRUNCATE TABLE ride;
INSERT INTO ride (id, driver_id, passenger_id, source_address, destination_address, ride_state, ride_date_time, ride_cost) VALUES (nextval('ride_id_seq'), '00000000-0000-0001-0000-000000000001','00000000-0000-0001-0000-000000000002', 'Source address', 'Destination address', 'CREATED', '2024-11-24 19:00', '1000');