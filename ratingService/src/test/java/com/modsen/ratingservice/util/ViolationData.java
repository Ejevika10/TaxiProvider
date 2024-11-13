package com.modsen.ratingservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ViolationData {
    public static final String USER_ID_MANDATORY = "userId: User id is mandatory";
    public static final String RIDE_ID_MANDATORY = "rideId: Ride id is mandatory";
    public static final String RATING_MANDATORY = "rating: Rating is mandatory";

    public static final String USER_ID_INVALID = "userId: must be greater than or equal to 0";
    public static final String RIDE_ID_INVALID = "rideId: must be greater than or equal to 0";
    public static final String RATING_INVALID = "rating: must be less than or equal to 5";

    public static final String OFFSET_INSUFFICIENT = "offset: must be greater than or equal to 0";
    public static final String LIMIT_INSUFFICIENT = "limit: must be greater than or equal to 1";
    public static final String LIMIT_EXCEEDED = "limit: must be less than or equal to 20";
}