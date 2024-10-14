package com.modsen.ratingservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    public static final String RATING_NOT_FOUND = "rating.notfound";

    public static final String RATING_FOR_RIDE_ALREADY_EXIST = "rating.ride.id";

    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";
}