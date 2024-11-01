package com.modsen.ratingservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    public static final String RATING_NOT_FOUND = "rating.notfound";

    public static final String RATING_FOR_RIDE_ALREADY_EXIST = "rating.ride.id";

    public static final String INVALID_STATE_VALUE = "Invalid value for State";

    public static final String DIFFERENT_DRIVERS_ID = "rating.drivers.different";

    public static final String DIFFERENT_PASSENGERS_ID = "rating.passengers.different";

    public static final String INVALID_RIDE_STATE = "rating.ride.state";

    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";

    public static final String SERVICE_UNAVAILABLE = "service.unavailable";
}