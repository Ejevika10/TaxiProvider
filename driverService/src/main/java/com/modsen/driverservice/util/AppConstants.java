package com.modsen.driverservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    public static final String PHONE_REGEXP = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
    public static final String UUID_REGEXP = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static final String INVALID_ROLE_VALUE = "invalid.user.role";

    public static final String CAR_NOT_FOUND = "car.notfound";
    public static final String CAR_NUMBER_EXIST = "car.number.exist";

    public static final String DRIVER_NOT_FOUND = "driver.notfound";
    public static final String DRIVER_EMAIL_EXIST = "driver.email.exist";

    public static final String FORBIDDEN = "forbidden";
    public static final String UNAUTHORIZED = "unauthorized";
}