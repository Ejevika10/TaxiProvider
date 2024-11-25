TRUNCATE TABLE ride;
ALTER SEQUENCE ride_id_seq RESTART WITH 1;
INSERT INTO ride (id, driver_id, passenger_id, source_address, destination_address, ride_state, ride_date_time, ride_cost) VALUES (nextval('ride_id_seq'), 1, 1, 'Source address', 'Destination address', 'CREATED', '2024-11-24 19:00', '1000')