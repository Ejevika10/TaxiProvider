package com.modsen.driverservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageConstants {
    public static final String INVALID_ROLE_VALUE = "invalid.user.role";

    public static final String CAR_NOT_FOUND = "car.notfound";
    public static final String CAR_NUMBER_EXIST = "car.number.exist";

    public static final String DRIVER_NOT_FOUND = "driver.notfound";
    public static final String DRIVER_EMAIL_EXIST = "driver.email.exist";

    public static final String FORBIDDEN = "forbidden";
    public static final String UNAUTHORIZED = "unauthorized";

    public static final String AVATAR_NOT_FOUND = "avatar.notfound";
    public static final String INVALID_FILE_TYPE = "invalid.file.type";

    public static final String SERVICE_UNAVAILABLE = "service.unavailable";
}
