package com.modsen.ratingservice.util;

public class E2ETestData {
    public static final String URL_DRIVER_RATING = "http://localhost:8080/api/v1/driverratings";
    public static final String URL_DRIVER_RATING_ID = URL_DRIVER_RATING + "/{ratingId}";
    public static final String URL_DRIVER_RATING_USER_ID = URL_DRIVER_RATING + "/user/{userId}";
    public static final String URL_PASSENGER_RATING = "http://localhost:8080/api/v1/passengerratings";
    public static final String URL_PASSENGER_RATING_ID = URL_PASSENGER_RATING + "/{ratingId}";
    public static final String URL_PASSENGER_RATING_USER_ID = URL_PASSENGER_RATING + "/user/{userId}";
}