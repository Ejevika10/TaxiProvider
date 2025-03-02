package com.modsen.passengerservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageConstants {
    public static final String PASSENGER_NOT_FOUND = "passenger.notfound";
    public static final String PASSENGER_EMAIL_EXISTS = "passenger.email.exist";

    public static final String INVALID_ROLE_VALUE = "invalid.user.role";

    public static final String FORBIDDEN = "forbidden";
    public static final String UNAUTHORIZED = "unauthorized";

    public static final String AVATAR_NOT_FOUND = "avatar.notfound";
    public static final String SERVICE_UNAVAILABLE = "service.unavailable";
    public static final String INVALID_FILE_TYPE = "invalid.file.type";
}
