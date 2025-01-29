package com.modsen.passengerservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {
    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";
    public static final String PASSENGER_NOT_FOUND = "passenger.notfound";
    public static final String PASSENGER_EMAIL_EXISTS = "passenger.email.exist";

    public static final String FORBIDDEN = "forbidden";
    public static final String UNAUTHORIZED = "unauthorized";

    public static final String PHONE_REGEXP="^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
}
