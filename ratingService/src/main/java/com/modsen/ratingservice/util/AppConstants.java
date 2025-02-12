package com.modsen.ratingservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    public static final String UUID_REGEXP = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static final String RATING_NOT_FOUND = "rating.notfound";

    public static final String RATING_FOR_RIDE_ALREADY_EXIST = "rating.ride.id";

    public static final String INVALID_STATE_VALUE = "invalid.state";

    public static final String INVALID_ROLE_VALUE = "invalid.user.role";

    public static final String DIFFERENT_DRIVERS_ID = "rating.drivers.different";

    public static final String DIFFERENT_PASSENGERS_ID = "rating.passengers.different";

    public static final String INVALID_RIDE_STATE = "rating.ride.state";

    public static final String SERVICE_UNAVAILABLE = "service.unavailable";

    public static final String FORBIDDEN = "forbidden";

    public static final String UNAUTHORIZED = "unauthorized";
}