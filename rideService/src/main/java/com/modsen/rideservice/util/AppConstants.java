package com.modsen.rideservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    public static final String UUID_REGEXP = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static final String RIDE_NOT_FOUND = "ride.notfound";

    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";

    public static final String INVALID_STATE_VALUE = "Invalid value for State";

    public static final String INVALID_ROLE_VALUE = "Invalid value for User Role";

    public static final String STATE_VALUE_ERROR = "ride.state.error";

    public static final String SERVICE_UNAVAILABLE = "service.unavailable";

    public static final String FORBIDDEN = "forbidden";

    public static final String UNAUTHORIZED = "unauthorized";

    public static final String BODY_READ_ERROR = "body.read.error";
}