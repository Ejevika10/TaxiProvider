package com.modsen.rideservice.util;

public class E2ETestData {
    public static final String URL_RIDE = "http://localhost:8081/api/v1/rides";
    public static final String URL_RIDE_ID = URL_RIDE + "/{rideId}";
    public static final String URL_RIDE_ID_STATE = URL_RIDE + "/{rideId}/state";

    public static final String URL_RIDE_DRIVER_ID = URL_RIDE + "/driver/{driverId}";
    public static final String URL_RIDE_PASSENGER_ID = URL_RIDE + "/passenger/{passengerId}";
}
