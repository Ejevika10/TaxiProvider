package com.modsen.driverservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    public static final String CAR_NOT_FOUND = "car.notfound";
    public static final String CAR_NUMBER_EXIST = "car.number.exist";

    public static final String DRIVER_NOT_FOUND = "driver.notfound";
    public static final String DRIVER_EMAIL_EXIST = "driver.email.exist";

    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";
}