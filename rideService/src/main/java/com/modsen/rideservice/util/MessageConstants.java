package com.modsen.rideservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageConstants {
    public static final String RIDE_NOT_FOUND = "ride.notfound";

    public static final String INVALID_STATE_VALUE = "invalid.state";

    public static final String INVALID_ROLE_VALUE = "invalid.user.role";

    public static final String STATE_VALUE_ERROR = "ride.state.error";

    public static final String SERVICE_UNAVAILABLE = "service.unavailable";

    public static final String FORBIDDEN = "forbidden";

    public static final String UNAUTHORIZED = "unauthorized";

    public static final String BODY_READ_ERROR = "body.read.error";
}