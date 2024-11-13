package com.modsen.driverservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ViolationData {
    public static final String CAR_MODEL_MANDATORY = "model: Model is mandatory";
    public static final String CAR_COLOR_MANDATORY = "color: Color is mandatory";
    public static final String CAR_BRAND_MANDATORY = "brand: Brand is mandatory";
    public static final String CAR_NUMBER_MANDATORY = "number: Number is mandatory";

    public static final String CAR_MODEL_INVALID = "model: size must be between 2 and 50";
    public static final String CAR_COLOR_INVALID = "color: size must be between 2 and 50";
    public static final String CAR_BRAND_INVALID = "brand: size must be between 2 and 50";
    public static final String CAR_NUMBER_INVALID = "number: size must be between 2 and 20";
    public static final String DRIVER_ID_INVALID = "driverId: must be greater than or equal to 0";

    public static final String DRIVER_NAME_MANDATORY = "name: Name is mandatory";
    public static final String DRIVER_EMAIL_MANDATORY = "email: Email is mandatory";
    public static final String DRIVER_PHONE_MANDATORY = "phone: Phone is mandatory";

    public static final String DRIVER_NAME_INVALID = "name: size must be between 4 and 100";
    public static final String DRIVER_EMAIL_INVALID = "email: Email is invalid";
    public static final String DRIVER_PHONE_INVALID = "phone: Phone is invalid";

    public static final String ID_INVALID = "id: must be greater than or equal to 0";
    public static final String OFFSET_INSUFFICIENT = "offset: must be greater than or equal to 0";
    public static final String LIMIT_INSUFFICIENT = "limit: must be greater than or equal to 1";
    public static final String LIMIT_EXCEEDED = "limit: must be less than or equal to 20";
}