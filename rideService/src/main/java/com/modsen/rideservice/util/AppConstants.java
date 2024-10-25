package com.modsen.rideservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    public static final String RIDE_NOT_FOUND = "ride.notfound";

    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";

    public static final String INVALID_STATE_VALUE = "Invalid value for State";

    public static final String STATE_VALUE_ERROR = "ride.state.error";

    public static final String SERVICE_UNAVAILABLE = "service.unavailable";
}